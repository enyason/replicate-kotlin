package io.github.enyason.client

import io.github.enyason.predictable.Predictable

interface Replicate {
    companion object {
        fun client(token: String): Replicate {
            return ReplicateClient(
                apiToken = token,
                predictionAPI = Unit,
                trainingAPI = Unit
            )
        }

    }

    suspend fun createPrediction(predictable: Predictable): Any

    suspend fun getPrediction(predictionId: String): Any

    suspend fun getPredictions(): List<String>

    suspend fun cancelPrediction(predictionId: String)

}