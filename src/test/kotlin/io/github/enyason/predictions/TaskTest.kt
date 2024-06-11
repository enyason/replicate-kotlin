package io.github.enyason.predictions

import io.github.enyason.client.polling.PollingStrategy
import io.github.enyason.client.task.Task
import io.github.enyason.client.task.await
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.models.PredictionStatus
import io.github.enyason.domain.predictions.models.isCanceled
import io.github.enyason.domain.predictions.models.isCompleted
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskTest {

    @Test
    fun `Given a new prediction, When await succeeds, Then a success status is returned`() = runTest {
        val predictionId = "xyzRTfj56"
        val output = listOf("image_url")
        val predictionStarting = Prediction<List<String>>(id = predictionId, status = PredictionStatus.STARTING)
        val predictionSucceeded = Prediction(id = predictionId, output = output, status = PredictionStatus.SUCCEEDED)
        val pollingStrategy = mockk<PollingStrategy<Prediction<List<String>>>>()

        coEvery { pollingStrategy.pollTask(any(), any()) } returns Task.success(
            predictionSucceeded,
            isComplete = predictionSucceeded.isCompleted(),
            isCanceled = predictionSucceeded.isCanceled(),
            pollingStrategy = pollingStrategy
        )

        val sut = with(predictionStarting) {
            Task.success(
                this,
                isComplete = isCompleted(),
                isCanceled = isCanceled(),
                pollingStrategy = pollingStrategy
            )
        }

        val result = sut.await()

        coVerify { pollingStrategy.pollTask(any(), any()) }
        assertEquals(true, result?.isCompleted())
        assertEquals(PredictionStatus.SUCCEEDED, result?.status)
        assertEquals(true, (result?.output is List<String>))
    }

    @Test
    fun `Given a new prediction, When await fails due to server error, Then a failure status is returned`() = runTest {
        val predictionId = "xyzRTfj56"
        val predictionStarting = Prediction<Any>(id = predictionId, status = PredictionStatus.STARTING)
        val predictionFailed = Prediction<Any>(id = predictionId, status = PredictionStatus.FAILED)
        val pollingStrategy = mockk<PollingStrategy<Prediction<Any>>>()

        coEvery { pollingStrategy.pollTask(any(), any()) } returns Task.success(
            predictionFailed,
            isComplete = predictionFailed.isCompleted(),
            isCanceled = predictionFailed.isCanceled(),
            pollingStrategy = pollingStrategy
        )

        val sut = with(predictionStarting) {
            Task.success(
                this,
                isComplete = isCompleted(),
                isCanceled = isCanceled(),
                pollingStrategy = pollingStrategy
            )
        }

        val result = sut.await()

        coVerify { pollingStrategy.pollTask(any(), any()) }
        assertEquals(PredictionStatus.FAILED, result?.status)
    }

    @Test
    fun `Given a canceled prediction, When await is called, Then same prediction is returned with canceled status`() =
        runTest {
            val predictionId = "xyzRTfj56"
            val predictionCancelled = Prediction<Any>(id = predictionId, status = PredictionStatus.CANCELED)
            val pollingStrategy = mockk<PollingStrategy<Prediction<Any>>>()

            val sut =
                with(predictionCancelled) {
                    Task.success(
                        this,
                        isComplete = isCompleted(),
                        isCanceled = isCanceled(),
                        pollingStrategy = pollingStrategy
                    )
                }

            val result = sut.await()

            coVerify(exactly = 0) { pollingStrategy.pollTask(predictionId, any()) }
            assertEquals(PredictionStatus.CANCELED, result?.status)
        }
}
