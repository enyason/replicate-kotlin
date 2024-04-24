package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.io.github.enyason.predictable.validate
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.createPrediction

class ReplicateClient(
    private val predictionAPI: PredictionsApi
) : Replicate {

    override suspend fun createPrediction(predictable: Predictable): Result<Prediction> {
        try {
            predictable.validate()
            val request = mapOf(
                "version" to predictable.versionId,
                "input" to predictable.input
            )
            val prediction = predictionAPI.createPrediction(request)
            return Result.success(prediction)
        } catch (error: Exception) {
            return Result.failure(error)
        }
    }

    override suspend fun getPrediction(predictionId: String): Result<Prediction> {
        if (predictionId.isEmpty()) throw IllegalArgumentException("Provided an empty prediction ID")
        return Result.failure(Throwable())
    }

    override suspend fun getPredictions(): Result<List<Prediction>> {
        return Result.success(emptyList())
    }

    override suspend fun cancelPrediction(predictionId: String): Result<Unit> {
        if (predictionId.isEmpty()) throw IllegalArgumentException("Provided an empty prediction ID")
        return Result.success(Unit)
    }
}
