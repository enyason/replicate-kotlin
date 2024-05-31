package io.github.enyason.client

import io.github.enyason.client.polling.PollingStrategy
import io.github.enyason.domain.models.Prediction

/**
 * [Task] defines an object type that holds information about an execution from [io.github.enyason.client.Replicate]
 * @author <a href="https://github.com/enyason">Emmanuel Enya </a>
 */
data class Task<RESULT>(

    /**
     * The result of the task executed
     */
    val result: RESULT?,

    /**
     * The error which occurred from executing the task. It can be null
     */
    val exception: Exception?,

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
    val pollingStrategy: PollingStrategy<RESULT>?

) {

    companion object {

        fun <RESULT> success(
            result: RESULT,
            isComplete: Boolean,
            isCanceled: Boolean,
            pollingStrategy: PollingStrategy<RESULT>
        ): Task<RESULT> {
            return Task(
                result = result,
                exception = null,
                isComplete = isComplete,
                isCanceled = isCanceled,
                pollingStrategy = pollingStrategy
            )
        }

        fun <RESULT> error(error: Exception?): Task<RESULT> {
            return Task(
                result = null,
                exception = error,
                isCanceled = false,
                isComplete = false,
                pollingStrategy = null
            )
        }
    }
}
