package io.github.enyason.client

import io.github.enyason.io.github.enyason.predictable.validate
import io.github.enyason.predictable.Predictable

class ReplicateClient(
    private val apiToken: String,
    private val predictionAPI: Unit,
    private val trainingAPI: Unit
) : Replicate {

    override suspend fun createPrediction(predictable: Predictable) {
        predictable.validate()

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