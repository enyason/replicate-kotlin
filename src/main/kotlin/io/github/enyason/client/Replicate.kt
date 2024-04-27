package io.github.enyason.client

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi

/**
 * This interface defines the actions for [Prediction], possible from https://replicate.com API
 * To create a replicate client:
 * val client = Replicate.client("token")
 *
 *  @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 */
interface Replicate {
    companion object {

        /**
         * create a replicate client using authorization code
         * @param token
         */
        fun client(token: String): Replicate {
            return ReplicateClient(predictionAPI = PredictionsApi(token))
        }

        /**
         * create a replicate client using [ReplicateConfig]
         * @param config
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
    suspend fun createPrediction(predictable: Predictable): Result<Prediction>

    suspend fun getPrediction(predictionId: String): Result<Prediction>

    suspend fun getPredictions(): Result<List<Prediction>>

    suspend fun cancelPrediction(predictionId: String): Result<Unit>
}
