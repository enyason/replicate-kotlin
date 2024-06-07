package io.github.enyason.predictions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.toModel
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
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

    suspend fun createPrediction(modelOwner: String, modelName: String, requestBody: Map<String, Any>) = flow {
        try {
            val predictionResponse = service.createPrediction(modelOwner, modelName, requestBody)
            if (!predictionResponse.isSuccessful) {
                emit(predictionResponse.errorBody()?.toModel()?.detail ?: "Could not create prediction with model name: $modelName")
                return@flow
            }

            predictionResponse.body()?.let { predictionDto ->
                EventSources
                    .createFactory(client())
                    .newEventSource(
                        request = request(predictionDto.urls?.stream.orEmpty()),
                        listener = listener { emit(it) }
                    )
            }
        } catch (exception: Exception) {
            emit(exception.localizedMessage)
        }
    }

    private fun listener(onEvent: suspend (String) -> Unit): EventSourceListener {
        val eventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
                println("Connection Opened")
            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                println("Connection Closed")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                println(data)
//                onEvent(data)
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                println("On Failure -: ${t?.message}")
            }
        }
        return eventSourceListener
    }

    private fun client() = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    private fun request(url: String): Request {
        println("STREAM URL is: $url")
        return Request.Builder()
            .url(url)
            .addHeader("Accept", "text/event-stream")
            .build()
    }
}
