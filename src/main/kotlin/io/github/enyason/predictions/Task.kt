package io.github.enyason.predictions

import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.isCanceled
import io.github.enyason.domain.models.isCompleted
import io.github.enyason.predictions.PredictionsApi

/**
 * [Task] defines an object type that holds information about an execution from [io.github.enyason.client.Replicate]
 * @author <a href="https://github.com/enyason">Emmanuel Enya </a>
 */
interface Task<T> {

    /**
     * The result of the task executed
     */
    val result: T

    /**
     * The error which occurred from executing the task. It can be null
     */
    val exception: Exception?

    /**
     * isSuccessful determines whether the execution failed or not
     */
    val isSuccessful: Boolean

    /**
     * isComplete is TRUE when the execution is successful and the task has a valid result
     * For a [Prediction] it would mean the status is [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED]
     */
    val isComplete: Boolean

    /**
     * isCanceled is TRUE when the prediction task has been canceled by the user
     */
    val isCanceled: Boolean

}

data object FailedTaskResult

class DefaultTask<T>(
    override val result: T,
    override val exception: Exception?,
    override val isSuccessful: Boolean,
    override val isComplete: Boolean,
    override val isCanceled: Boolean
) : Task<T> {

    companion object {

        fun <T> success(result: T, isComplete: Boolean, isCanceled: Boolean): Task<T> {
            return DefaultTask(
                result = result,
                isSuccessful = true,
                exception = null,
                isComplete = isComplete,
                isCanceled = isCanceled
            )
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> error(error: Exception?): Task<T> {
            return DefaultTask(
                result = FailedTaskResult as T,
                exception = error,
                isSuccessful = false,
                isCanceled = false,
                isComplete = false
            )
        }
    }
}
