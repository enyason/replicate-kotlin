package io.github.enyason.client

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.domain.models.Prediction
import io.github.enyason.io.github.enyason.predictions.Task
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi

/**
 * This interface defines the actions for [Prediction], possible from [Replicate](https://replicate.com) API
 *
 * To create a replicate client:
 *
 * val client = Replicate.client("token")
 *
 *          or
 *
 * val client = Replicate.client(ReplicateConfig("token"))
 *
 *  @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 */
interface Replicate {
    companion object {

        /**
         * create a replicate client using authorization code
         * @param token API token from [Replicate](https://replicate.com) API
         */
        fun client(token: String): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(token))
        }

        /**
         * create a replicate client using [ReplicateConfig]
         * @param config A set of configurations used to control the way the SDK behaves
         */
        fun client(config: ReplicateConfig): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(config))
        }
    }

    /**
     * Start a new prediction for the model version and inputs you provide
     * in the [Predictable]
     * @see Predictable
     */
    suspend fun createPrediction(predictable: Predictable): Task<Prediction>

    /**
     * Get the current state of a prediction identified by the provided predictionId
     * @param predictionId The Prediction's ID
     */
    suspend fun getPrediction(predictionId: String): Task<Prediction>

    suspend fun getPredictions(): Result<List<Prediction>>

    /**
     * Cancel an ongoing Prediction identified by the provided predictionId
     * @param predictionId The ID of the prediction to be cancelled
     */
    suspend fun cancelPrediction(predictionId: String): Result<Boolean>
}
