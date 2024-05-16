package io.github.enyason.predictions

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictions.models.toModel

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

    private val service by lazy { retrofit.create(PredictionsApiService::class.java) }

    suspend fun createPrediction(requestBody: Map<String, Any>): Pair<Prediction?, Exception?> {
        val response = service.createPrediction(requestBody)
        val prediction = response.body()?.toPrediction()
        return if (response.isSuccessful && prediction != null) {
            Pair(prediction, null)
        } else {
            val error = response.errorBody()?.toModel()
            val message = error?.detail ?: "Could not create prediction"
            Pair(null, IllegalStateException(message))
        }
    }

    suspend fun getPrediction(predictionId: String): Pair<Prediction?, Exception?> {
        val response = service.getPrediction(predictionId)
        val prediction = response.body()?.toPrediction()
        return if (response.isSuccessful && prediction != null) {
            Pair(prediction, null)
        } else {
            val error = response.errorBody()?.toModel()
            val message = error?.detail ?: "Could not fetch prediction"
            Pair(null, IllegalStateException(message))
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
