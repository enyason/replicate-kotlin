package io.github.enyason.client

import com.google.gson.reflect.TypeToken
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.client.polling.PredictionPollingStrategy
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.models.isCanceled
import io.github.enyason.domain.predictions.models.isCompleted
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.predictable.Predictable
import io.github.enyason.predictions.predictable.validate
import io.github.enyason.predictions.predictable.validateId
import kotlinx.coroutines.flow.Flow

/**
 * A client class to interact with the [Replicate](https://replicate.com) API.
 * This class provides methods for creating, retrieving, and canceling predictions.
 *
 * Here is an example of how to get an instance of the class:
 *```
 *
 * val client = Replicate.client("token")
 *
 * or
 *
 * val client = Replicate.client(ReplicateConfig("token"))
 *```
 *
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
class Replicate(val predictionAPI: PredictionsApi) {
    /**
     * Creates a new Prediction for a specific model version.
     * This method takes a [Predictable] object that encapsulates the inputs for the model.
     *
     * @param <OUTPUT> The generic type representing the data structure of the prediction output.
     * @param predictable the object holding the model version and input data for the prediction
     * @param stream specify the stream option to request a URL to receive streaming output using server-sent events (SSE).
     * If the requested model version supports streaming, then the returned prediction will have a stream entry in its
     * urls property with a URL that you can use to stream the output using [io.github.enyason.client.stream]
     * @return a [Task] object representing the created prediction. You can call await on this object
     *         to wait for the prediction to complete and retrieve the results.
     * @see io.github.enyason.domain.models.Prediction.output
     */
    suspend inline fun <reified OUTPUT> createPrediction(
        predictable: Predictable,
        stream: Boolean = false,
    ): Task<Prediction<OUTPUT>> {
        return try {
            predictable.validate()
            val request =
                mapOf(
                    "version" to predictable.versionId,
                    "input" to predictable.input,
                    "stream" to stream,
                )

            val predictionDtoObjectType = object : TypeToken<PredictionDTO<OUTPUT>>() {}.type
            val (prediction, error) = predictionAPI.createPrediction<OUTPUT>(request, predictionDtoObjectType)

            when {
                prediction != null -> {
                    Task.success(
                        result = prediction,
                        isComplete = prediction.isCompleted(),
                        isCanceled = prediction.isCanceled(),
                        PredictionPollingStrategy(predictionAPI),
                    )
                }

                else -> {
                    Task.error(error)
                }
            }
        } catch (error: Exception) {
            Task.error(error)
        }
    }

    /**
     * Retrieves the current status and results of a Prediction identified by its ID.
     *
     * @param <OUTPUT> The generic type representing the data structure of the prediction output.
     * @param predictionId the ID of the prediction to retrieve
     * @return a [Task] object containing the retrieved prediction information.
     * @see io.github.enyason.domain.models.Prediction.output
     */
    suspend inline fun <reified OUTPUT> getPrediction(predictionId: String): Task<Prediction<OUTPUT>> {
        return try {
            predictionId.validateId()
            val predictionDtoObjectType = object : TypeToken<PredictionDTO<OUTPUT>>() {}.type
            val (prediction, error) = predictionAPI.getPrediction<OUTPUT>(predictionId, predictionDtoObjectType)

            when {
                prediction != null -> {
                    Task.success(
                        result = prediction,
                        isComplete = prediction.isCompleted(),
                        isCanceled = prediction.isCanceled(),
                        PredictionPollingStrategy(predictionAPI),
                    )
                }

                else -> {
                    Task.error(error)
                }
            }
        } catch (error: Exception) {
            Task.error(error)
        }
    }

    /**
     * Cancels an ongoing Prediction identified by its ID.
     *
     * @param predictionId the ID of the prediction to cancel
     * @return a [Result] object indicating success (true) or failure with an exception.
     */
    suspend fun cancelPrediction(predictionId: String): Result<Boolean> {
        return try {
            predictionId.validateId()
            val (isSuccess, error) = predictionAPI.cancelPrediction(predictionId)
            when {
                isSuccess -> Result.success(true)
                else -> Result.failure(error ?: Throwable())
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    fun getPredictions(): Result<List<Prediction<*>>> {
        TODO("Not yet implemented")
    }

    /**
     * Create a Prediction using a specific model with an opportunity to stream the output.
     *
     * @param modelOwner the name of the model owner
     * @param modelName the name of the model to be used
     * @param input the input for the model
     * @return a [Flow] of Strings representing the output produced as server-sent events (SSE).
     */
    suspend fun streamWithModel(
        modelOwner: String,
        modelName: String,
        input: Map<String, Any>,
    ): Flow<String> {
        val requestBody = mapOf("input" to input, "stream" to true)
        return predictionAPI.streamWithModel(modelOwner, modelName, requestBody)
    }

    /**
     * Create a Prediction using a specific deployment with an opportunity to stream the output.
     *
     * @param deploymentOwner the name of the deployment owner
     * @param deploymentName the name of the deployment to be used
     * @param input the input for the deployment
     * @return a [Flow] of Strings representing the output produced as server-sent events (SSE).
     */
    suspend fun streamWithDeployment(
        deploymentOwner: String,
        deploymentName: String,
        input: Map<String, Any>,
    ): Flow<String> {
        val requestBody = mapOf("input" to input, "stream" to true)
        return predictionAPI.streamWithDeployment(deploymentOwner, deploymentName, requestBody)
    }

    companion object {
        /**
         * create a replicate client using authorization code
         * @param token API token from [Replicate](https://replicate.com) API
         */
        fun client(token: String): Replicate {
            return Replicate(predictionAPI = PredictionsApi(token))
        }

        /**
         * create a replicate client using [ReplicateConfig]
         * @param config A set of configurations used to control the way the SDK behaves
         */
        fun client(config: ReplicateConfig): Replicate {
            return Replicate(predictionAPI = PredictionsApi(config))
        }
    }
}
