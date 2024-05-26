package io.github.enyason.io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.io.github.enyason.client.polling.PollingStrategy

/**
 * [Task] defines an object type that holds information about an execution from [io.github.enyason.client.Replicate]
 * @author <a href="https://github.com/enyason">Emmanuel Enya </a>
 */
data class Task<T>(

    /**
     * The result of the task executed
     */
    val result: T?,

    /**
     * The error which occurred from executing the task. It can be null
     */
    val exception: Exception?,

    /**
     * isSuccessful determines whether the execution failed or not
     */
    val isSuccessful: Boolean,

    /**
     * isComplete is TRUE when the execution is successful and the task has a valid result
     * For a [Prediction] it would mean the status is [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED]
     */
    val isComplete: Boolean,

    /**
     * isCanceled is TRUE when the prediction task has been canceled by the user
     */
    val isCanceled: Boolean,

    /**
     * The strategy used to poll for the task's completion status and update its information.
     */
    val pollingStrategy: PollingStrategy<T>?,

    ) {

    companion object {

        fun <T> success(
            result: T,
            isComplete: Boolean,
            isCanceled: Boolean,
            pollingStrategy: PollingStrategy<T>
        ): Task<T> {
            return Task(
                result = result,
                isSuccessful = true,
                exception = null,
                isComplete = isComplete,
                isCanceled = isCanceled,
                pollingStrategy = pollingStrategy
            )
        }

        fun <T> error(error: Exception?): Task<T> {
            return Task(
                result = null,
                exception = error,
                isSuccessful = false,
                isCanceled = false,
                isComplete = false,
                pollingStrategy = null
            )
        }
    }
}

