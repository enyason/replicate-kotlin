package io.github.enyason.io.github.enyason.predictions

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
    val result: T?

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
     */
    val isComplete: Boolean


    /**
     * isCanceled is TRUE when the prediction task has been canceled by the user
     */
    val isCanceled: Boolean

    /**
     * API service object which will be used to initiate server polling when [await] is called
     */
    val service: Any?
}

class DefaultTask<T>(
    override val result: T?,
    override val exception: Exception?,
    override val isSuccessful: Boolean,
    override val isComplete: Boolean,
    override val isCanceled: Boolean,
) : Task<T> {

    override var service: Any? = null


    companion object {

        fun success(prediction: Prediction, predictionAPI: PredictionsApi): Task<Prediction> {
            return DefaultTask(
                result = prediction,
                isSuccessful = true,
                exception = null,
                isCanceled = prediction.isCanceled(),
                isComplete = prediction.isCompleted(),
            ).apply { service = predictionAPI }
        }

        fun error(error: Exception?): Task<Prediction> {
            return DefaultTask(
                result = null,
                exception = error,
                isSuccessful = false,
                isCanceled = false,
                isComplete = false
            )
        }
    }
}