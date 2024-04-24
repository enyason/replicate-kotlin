package io.github.enyason.client

import io.github.enyason.domain.mappers.toPrediction
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
            val response = predictionAPI.createPrediction(request)
            println(response.isSuccessful)
            println(response.errorBody().toString())
            val prediction = response.body()?.toPrediction() ?: throw IllegalStateException("Could not create predication ")
            return Result.success(prediction)
        } catch (error: Exception) {
            return Result.failure(error)
        }
    }

    override suspend fun getPrediction(predictionId: String) {
        if (predictionId.isEmpty()) throw IllegalArgumentException("Provided an empty prediction ID")
    }

    override suspend fun getPredictions(): List<String> {
        return emptyList()
    }

    override suspend fun cancelPrediction(predictionId: String) {
        if (predictionId.isEmpty()) throw IllegalArgumentException("Provided an empty prediction ID")
    }
}
