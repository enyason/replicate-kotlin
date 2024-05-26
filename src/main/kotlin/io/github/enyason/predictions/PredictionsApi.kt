package io.github.enyason.predictions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.toModel
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume

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

    suspend fun <T> createPrediction(requestBody: Map<String, Any>, type: Type): Pair<Prediction<T>?, Exception?> {
        val responseBody = service.createPrediction(requestBody)
        return try {
            responseBody.use { body ->
                val reader = responseBody.charStream()
                val predictionDto = gson.fromJson<PredictionDTO<T>>(reader, type)
                val prediction = predictionDto.toPrediction()
                Pair(prediction, null)
            }
        } catch (exception: Exception) {
            Pair(null, exception)
        }
    }

    suspend fun <T> getPrediction(predictionId: String, type: Type): Pair<Prediction<T>?, Exception?> {
        val responseBody = service.getPrediction(predictionId)
        return try {
            responseBody.use { body ->
                val reader = body.charStream()
                val predictionDto = gson.fromJson<PredictionDTO<T>>(reader, type)
                val prediction = predictionDto.toPrediction()
                Pair(prediction, null)
            }
        } catch (exception: Exception) {
            Pair(null, exception)
        }
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

}
