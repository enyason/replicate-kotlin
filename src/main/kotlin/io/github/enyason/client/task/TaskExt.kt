package io.github.enyason.client.task

import com.google.gson.reflect.TypeToken
import io.github.enyason.client.polling.PredictionPollingStrategy.Companion.PREDICTION_OUTPUT_TYPE_ARG
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.predictions.models.PredictionDTO
import kotlin.coroutines.cancellation.CancellationException

/**
 * Awaits for the completion of a [Task] object representing a prediction.
 * This function repeatedly polls the prediction service (Replicate: https://replicate.com/)
 * using the provided [Task.pollingStrategy] until the prediction reaches a successful state
 * @see [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED].
 *
 * @return the final prediction object after successful completion,
 *         or throws an exception if the task encounters an error or is cancelled.
 * @throws CancellationException if the task was cancelled before completion.
 * @throws Exception if an error occurs while communicating with the prediction service.
 */
suspend inline fun <reified OUTPUT> Task<Prediction<OUTPUT>>.await(): Prediction<OUTPUT>? {
    if (isComplete || isCanceled) {
        return result
    }

    val predictionId = result?.id.orEmpty()

    val type = object : TypeToken<PredictionDTO<OUTPUT>>() {}.type
    val task =
        pollingStrategy?.pollTask(
            predictionId,
            mapOf(
                PREDICTION_OUTPUT_TYPE_ARG to type,
            ),
        )
    return task?.result
}
