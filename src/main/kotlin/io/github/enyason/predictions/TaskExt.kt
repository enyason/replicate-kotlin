import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus.*
import io.github.enyason.io.github.enyason.predictions.Task
import io.github.enyason.predictions.PredictionsApi
import io.github.enyason.predictions.PredictionsApiService
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

/**
 * Calling [await] on a [Task] polls <a href="https://replicate.com">Replicate</a> until the prediction status becomes
 * succeeded. See [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED]
 * [delayInMillis] is the time delay in milliseconds before another request to the server is triggered
 */
suspend fun Task<Prediction>.await(delayInMillis: Long = 2000): Prediction {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException("Task $this was cancelled.")
            } else {
                result as Prediction
            }
        } else {
            throw e
        }
    }


    val predictionsApiService = this.service as PredictionsApi

    var prediction = this.result
    val predictionId = result?.id
    if (prediction == null) {
        throw Exception("Prediction object is null")
    } else if (predictionId == null)
        throw Exception("Prediction ID is null")
    else {

        fun predictionStatus() = prediction?.status ?: STARTING

        suspend fun pollPrediction(predictionId: String) {
            delay(delayInMillis)
            println("polling prediction status")
            prediction = predictionsApiService.getPrediction(predictionId).first
        }

        while (true) {
            println(predictionStatus())
            when (predictionStatus()) {
                STARTING, PROCESSING -> pollPrediction(predictionId)
                SUCCEEDED, FAILED, CANCELED, UNKNOWN -> break
            }
        }

    }

    return prediction ?: throw Exception("Prediction object is null")
}