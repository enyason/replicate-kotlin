package io.github.enyason.client

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi

interface Replicate {
    companion object {
        fun client(token: String): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(token))
        }

        fun client(config: ReplicateConfig): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(config))
        }
    }

    suspend fun createPrediction(predictable: Predictable): Result<Prediction>

    suspend fun getPrediction(predictionId: String): Result<Prediction>

    suspend fun getPredictions(): Result<List<Prediction>>

    suspend fun cancelPrediction(predictionId: String): Result<Unit>
}
