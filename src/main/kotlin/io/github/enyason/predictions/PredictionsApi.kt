package io.github.enyason.predictions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.domain.predictions.models.PaginatedPredictions
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.toPaginatedPredictions
import io.github.enyason.domain.predictions.toPrediction
import io.github.enyason.predictions.models.PaginatedPredictionsDTO
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.toModel
import okhttp3.ResponseBody
import java.lang.reflect.Type

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

    private fun <OUTPUT> getResponse(responseBody: ResponseBody, type: Type) =
        responseBody.use { body ->
            val reader = body.charStream()
            val predictionDto = gson.fromJson<PredictionDTO<OUTPUT>>(reader, type)
            val prediction = predictionDto.toPrediction()
            Pair(prediction, null)
        }

    private fun getResponse(responseBody: ResponseBody) = responseBody.use { body ->
        val reader = body.charStream()
        val paginatedPredictionsDto = gson.fromJson(reader, PaginatedPredictionsDTO::class.java)
        val paginatedPredictions = paginatedPredictionsDto.toPaginatedPredictions()
        Pair(paginatedPredictions, null)
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

    suspend fun listPredictions(cursor: String): Pair<PaginatedPredictions?, Exception?> {
        return try {
            val responseBody = service.listPredictions(cursor)
            getResponse(responseBody)
        } catch (exception: Exception) {
            Pair(null, exception)
        }
    }
}
