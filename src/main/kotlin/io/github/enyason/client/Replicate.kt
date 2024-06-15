package io.github.enyason.client

import com.google.gson.reflect.TypeToken
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.client.polling.PredictionPollingStrategy
import io.github.enyason.client.task.Task
import io.github.enyason.domain.predictions.models.PaginatedPredictions
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.models.isCanceled
import io.github.enyason.domain.predictions.models.isCompleted
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.predictable.Predictable
import io.github.enyason.predictions.predictable.validate
import io.github.enyason.predictions.predictable.validateId

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
            val (prediction, error) = predictionAPI.createPrediction<OUTPUT>(
                request,
                predictionDtoObjectType
            )

            when {
                prediction != null -> {
                    Task.success(
                        result = prediction,
                        isComplete = prediction.isCompleted(),
                        isCanceled = prediction.isCanceled(),
                        PredictionPollingStrategy<OUTPUT>(predictionAPI)
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
            val (prediction, error) = predictionAPI.getPrediction<OUTPUT>(
                predictionId,
                predictionDtoObjectType
            )

            when {
                prediction != null -> {
                    Task.success(
                        result = prediction,
                        isComplete = prediction.isCompleted(),
                        isCanceled = prediction.isCanceled(),
                        PredictionPollingStrategy<OUTPUT>(predictionAPI)
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

    /**
     * Get a paginated list of predictions that you have created from the API and the website.
     * This method returns *100* records per page.
     * @param cursor A pointer to the page of predictions you want to fetch. When null it returns
     * the first page. Subsequent calls can use:
     * - the [PaginatedPredictions.next] url gotten from the response to retrieve
     *   the next page of results. You can use the helper extension [getCursor] on this url,
     *   to extract the cursor id.
     * - the [PaginatedPredictions.previous] url gotten from the response to retrieve
     *   the previous page of results. You can use the helper extension [getCursor] on this url,
     *   to extract the cursor id.
     * @return A [Result] object:
     * - [Result.success]: Contains a [PaginatedPredictions] object with the retrieved predictions
     * data if successful.
     * - [Result.failure]: Contains an error object with an exception.
     */
    suspend fun listPredictions(cursor: String? = null): Result<PaginatedPredictions> {
        return try {
            val (predictions, error) = predictionAPI.listPredictions(cursor)

            when {
                predictions != null -> {
                    Result.success(predictions)
                }

                else -> Result.failure(error ?: Throwable())
            }
        } catch (error: Exception) {
            Result.failure(error)
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
