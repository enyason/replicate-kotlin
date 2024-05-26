package io.github.enyason.io.github.enyason.client.polling

import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus
import io.github.enyason.domain.models.PredictionStatus.CANCELED
import io.github.enyason.domain.models.PredictionStatus.FAILED
import io.github.enyason.domain.models.PredictionStatus.SUCCEEDED
import io.github.enyason.domain.models.PredictionStatus.UNKNOWN
import io.github.enyason.domain.models.isCanceled
import io.github.enyason.domain.models.isCompleted
import io.github.enyason.io.github.enyason.client.Task
import io.github.enyason.predictions.PredictionsApi
import kotlinx.coroutines.delay
import java.lang.reflect.Type

/**
 * A concrete implementation of [PollingStrategy] specifically designed for polling prediction tasks.
 * This strategy utilizes a [PredictionsApi] instance to retrieve the status and results of a prediction.
 *
 * @param <OUTPUT> The generic type representing the data structure of the prediction output.
 * @see [Prediction.output]
 */
class PredictionPollingStrategy<OUTPUT>(
    /**
     * The [PredictionsApi] used to interact with the prediction service and retrieve updates.
     */
    private val predictionsApi: PredictionsApi,

    /**
     * The delay (in milliseconds) between polling attempts for prediction status updates.
     * Defaults to the value retrieved from [PredictionsApi.pollingDelayInMillis].
     */
    private val pollingDelayInMillis: Long = predictionsApi.pollingDelayInMillis
) : PollingStrategy<Prediction<OUTPUT>> {

    /**
     * Polls for the status and result of a prediction task.
     * This method repeatedly retrieves the prediction information using the provided `taskId`
     * until the prediction reaches a terminal state ([SUCCEEDED], [FAILED], [CANCELED]) or an error occurs.
     *
     * @param taskId the ID of the prediction task to poll
     * @param extraArgs optional arguments that might be used during polling (e.g., specifying prediction output type)
     * @return an updated `Task` object reflecting the final state of the prediction after polling
     */
    override suspend fun pollTask(taskId: String, extraArgs: Map<String, Any>?): Task<Prediction<OUTPUT>> {
        val type: Type = extraArgs?.get(PREDICTION_OUTPUT_TYPE_ARG) as Type
        var status: PredictionStatus? = PredictionStatus.STARTING
        var polledPrediction: Prediction<OUTPUT>? = null
        var exception: Exception? = null

        while (true) {
            when (status) {
                PredictionStatus.STARTING, PredictionStatus.PROCESSING -> {
                    delay(pollingDelayInMillis)
                    val (prediction, error) = predictionsApi.getPrediction<OUTPUT>(taskId, type)
                    if (error != null || prediction == null) {
                        exception = error
                        break
                    }
                    polledPrediction = prediction
                    status = prediction.status
                }

                SUCCEEDED, FAILED, CANCELED, UNKNOWN, null -> break
            }
        }

        return Task(
            result = polledPrediction,
            exception = exception,
            isSuccessful = true,
            isComplete = polledPrediction?.isCompleted() ?: false,
            isCanceled = polledPrediction?.isCanceled() ?: false,
            pollingStrategy = this
        )
    }

    companion object {
        const val PREDICTION_OUTPUT_TYPE_ARG = "prediction_output_type"
    }
}
