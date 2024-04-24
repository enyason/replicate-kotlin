package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi

interface Replicate {
    companion object {
        fun client(token: String): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(token))
        }
    }

    suspend fun createPrediction(predictable: Predictable): Result<Prediction>

    suspend fun getPrediction(predictionId: String): Result<Prediction>

    suspend fun getPredictions(): Result<List<Prediction>>

    suspend fun cancelPrediction(predictionId: String): Result<Unit>
}
