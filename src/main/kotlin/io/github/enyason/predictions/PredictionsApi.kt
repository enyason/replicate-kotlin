package io.github.enyason.predictions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.base.StreamingEventSourceListener
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.toModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.sse.EventSources
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * This class receives and processes the responses gotten from [Replicate's](https://replicate.com) Predictions API
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 */
class PredictionsApi(config: ReplicateConfig) {

    constructor(apiToken: String) : this(ReplicateConfig(apiToken = apiToken))

    private val retrofit by lazy { RetrofitFactory.buildRetrofit(config) }

    private val service: PredictionsApiService by lazy { retrofit.create(PredictionsApiService::class.java) }

    private val gson: Gson by lazy { GsonBuilder().create() }

    val pollingDelayInMillis = config.pollingDelayInMillis

    suspend fun <OUTPUT> createPrediction(
        requestBody: Map<String, Any>,
        type: Type
    ): Pair<Prediction<OUTPUT>?, Exception?> {
        return try {
            val responseBody = service.createPrediction(requestBody)
            getResponse(responseBody, type)
        } catch (exception: Exception) {
            Pair(null, exception)
        }
    }

    suspend fun <OUTPUT> getPrediction(predictionId: String, type: Type): Pair<Prediction<OUTPUT>?, Exception?> {
        return try {
            val responseBody = service.getPrediction(predictionId)
            getResponse(responseBody, type)
        } catch (exception: Exception) {
            Pair(null, exception)
        }
    }

    private fun <OUTPUT> getResponse(responseBody: ResponseBody, type: Type) = responseBody.use { body ->
        val reader = body.charStream()
        val predictionDto = gson.fromJson<PredictionDTO<OUTPUT>>(reader, type)
        val prediction = predictionDto.toPrediction()
        Pair(prediction, null)
    }

    suspend fun cancelPrediction(predictionId: String): Pair<Boolean, Exception?> {
        val response = service.cancelPrediction(predictionId)
        return if (response.isSuccessful) {
            Pair(true, null)
        } else {
            val error = response.errorBody()?.toModel()
            val message = error?.detail ?: "Could not cancel prediction with ID: $predictionId"
            Pair(false, IllegalStateException(message))
        }
    }

    suspend fun stream(modelOwner: String, modelName: String, requestBody: Map<String, Any>) =
        callbackFlow {
            val predictionResponse = service.createPrediction(modelOwner, modelName, requestBody)
            if (!predictionResponse.isSuccessful) {
                val errorMessage = predictionResponse.errorBody()?.toModel()?.detail
                    ?: "Could not create prediction with model name: $modelName"
                throw Exception(errorMessage)
            }
            predictionResponse.body()?.let { predictionDto ->
                EventSources
                    .createFactory(sseClient())
                    .newEventSource(
                        request = sseRequest(predictionDto.urls?.stream ?: throw Exception("Stream URL is null.")),
                        listener = StreamingEventSourceListener { data -> this.trySend(data) }
                    )
            }

            awaitClose()
        }

    companion object {
        private fun sseRequest(url: String): Request {
            return Request.Builder()
                .url(url)
                .addHeader("Accept", "text/event-stream")
                .build()
        }

        private fun sseClient() = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()
    }
}
