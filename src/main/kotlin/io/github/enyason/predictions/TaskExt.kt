import io.github.enyason.client.ReplicateClient
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus.CANCELED
import io.github.enyason.domain.models.PredictionStatus.FAILED
import io.github.enyason.domain.models.PredictionStatus.PROCESSING
import io.github.enyason.domain.models.PredictionStatus.STARTING
import io.github.enyason.domain.models.PredictionStatus.SUCCEEDED
import io.github.enyason.domain.models.PredictionStatus.UNKNOWN
import io.github.enyason.predictions.Task
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

/**
 * Calling [await] on a [Task] polls <a href="https://replicate.com">Replicate</a> until the prediction status becomes
 * succeeded. See [io.github.enyason.domain.models.PredictionStatus.SUCCEEDED]
 * [delayInMillis] is the time delay in milliseconds before another request to the server is triggered
 */
suspend fun Task<Prediction>.await(client: ReplicateClient, delayInMillis: Long = 2000): Prediction {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException("Task $this was cancelled.")
            } else {
                result
            }
        } else {
            throw e
        }
    }

    delay(delayInMillis)

    val predictionId = result.id
    val task = client.getPrediction(predictionId)

    return when (task.result.status) {
        STARTING, PROCESSING -> task.await(client, delayInMillis)
        SUCCEEDED, FAILED, CANCELED, UNKNOWN, null -> task.result
    }
}
