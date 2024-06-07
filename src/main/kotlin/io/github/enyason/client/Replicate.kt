package io.github.enyason.client

import com.google.gson.reflect.TypeToken
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.client.polling.PredictionPollingStrategy
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.isCanceled
import io.github.enyason.domain.models.isCompleted
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictable.validate
import io.github.enyason.predictable.validateId
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.models.PredictionDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A client class to interact with the [Replicate](https://replicate.com) API.
 * This class provides methods for creating, retrieving, and canceling predictions.
 *
 *<p>Here is an example on how to get an intance of the class ,
 *<pre>
 *
 * val client = Replicate.client("token")
 *
 * or
 *
 * val client = Replicate.client(ReplicateConfig("token"))
 *</pre>
 *
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
class Replicate(val predictionAPI: PredictionsApi) {

    /**
     * Creates a new prediction for a specific model version.
     * This method takes a [Predictable] object that encapsulates the inputs for the model.
     *
     * @param <OUTPUT> The generic type representing the data structure of the prediction output.
     * @param predictable the object holding the model version and input data for the prediction
     * @return a [Task] object representing the created prediction. You can call await on this object
     *         to wait for the prediction to complete and retrieve the results.
     * @see io.github.enyason.domain.models.Prediction.output
     */
    suspend inline fun <reified OUTPUT> createPrediction(predictable: Predictable): Task<Prediction<OUTPUT>> {
        return try {
            predictable.validate()
            val request = mapOf(
                "version" to predictable.versionId,
                "input" to predictable.input
            )

            val predictionDtoObjectType = object : TypeToken<PredictionDTO<OUTPUT>>() {}.type
            val (prediction, error) = predictionAPI.createPrediction<OUTPUT>(request, predictionDtoObjectType)

            when {
                prediction != null -> {
                    Task.success(
                        result = prediction,
                        isComplete = prediction.isCompleted(),
                        isCanceled = prediction.isCanceled(),
                        PredictionPollingStrategy(predictionAPI)
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
     * Retrieves the current status and results of a prediction identified by its ID.
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
                        PredictionPollingStrategy(predictionAPI)
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
     * Cancels an ongoing prediction identified by its ID.
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

    suspend fun createPrediction(modelOwner: String, modelName: String, requestBody: Map<String, Any>): Flow<String> {
        requestBody.toMutableMap()["stream"] = true
        return try {
            predictionAPI.createPrediction(modelOwner, modelName, requestBody)
        } catch (error: Exception) {
            flowOf(error.localizedMessage)
        }
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
