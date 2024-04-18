package io.github.enyason.client

import io.github.enyason.io.github.enyason.predictable.validate
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.createPrediction
import retrofit2.Response

class ReplicateClient(
    private val predictionAPI: PredictionsApi,
) : Replicate {

    override suspend fun createPrediction(predictable: Predictable): Response<Any?> {
        predictable.validate()
        return predictionAPI.createPrediction()

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