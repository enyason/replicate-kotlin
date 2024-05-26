package io.github.enyason.io.github.enyason.client

import com.google.gson.reflect.TypeToken
import io.github.enyason.domain.models.Prediction
import io.github.enyason.io.github.enyason.client.polling.PredictionPollingStrategy.Companion.PREDICTION_OUTPUT_TYPE_ARG
import io.github.enyason.predictions.models.PredictionDTO
import kotlin.coroutines.cancellation.CancellationException

/**
 * Awaits for the completion of a [Task] object representing a prediction.
 * This function repeatedly polls the prediction service (Replicate: https://replicate.com/)
 * using the provided [pollingStrategy] until the prediction reaches a successful state
 * @see [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED].
 *
 * @return the final prediction object after successful completion,
 *         or throws an exception if the task encounters an error or is cancelled.
 * @throws CancellationException if the task was cancelled before completion.
 * @throws Exception if an error occurs while communicating with the prediction service.
 */
suspend inline fun <reified T> Task<Prediction<T>>.await(): Prediction<T>? {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException("Task $this was cancelled.")
            } else {
                result!!
            }
        } else {
            throw e
        }
    }

    val predictionId = result?.id.orEmpty()

    val type = object : TypeToken<PredictionDTO<T>>() {}.type
    val task = pollingStrategy?.pollTask(
        predictionId, mapOf(
            PREDICTION_OUTPUT_TYPE_ARG to type
        )
    )
    return task?.result
}