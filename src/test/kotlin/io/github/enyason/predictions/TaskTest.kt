package io.github.enyason.predictions

import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus
import io.github.enyason.domain.models.isCanceled
import io.github.enyason.domain.models.isCompleted
import io.github.enyason.io.github.enyason.client.Task
import io.github.enyason.io.github.enyason.client.await
import io.github.enyason.io.github.enyason.client.polling.PollingStrategy
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

        println(result?.status)

        assertEquals(true, result?.isCompleted())
        assertEquals(PredictionStatus.SUCCEEDED, result?.status)
        assertEquals(true, (result?.output is List<String>))

        coVerify {
            pollingStrategy.pollTask(any(), any())
        }
    }

//
//    @Test
//    fun `Given a new prediction, When await fails due to server error, Then a failure status is returned`() = runTest {
//        val predictionId = "xyzRTfj56"
//        val predictionStarting = Prediction<String>(id = predictionId, status = PredictionStatus.STARTING)
//        val predictionProcessing = Prediction<String>(id = predictionId, status = PredictionStatus.PROCESSING)
//        val predictionSucceeded = Prediction<String>(id = predictionId, status = PredictionStatus.FAILED)
//        val client = mockk<Replicate>()
//
//        coEvery { client.getPrediction<String>(predictionId) } returnsMany listOf(
//            Task.success(predictionProcessing, isComplete = false, isCanceled = false),
//            Task.success(predictionSucceeded, isComplete = true, isCanceled = false)
//        )
//
//        val sut = with(predictionStarting) { Task.success(this, isComplete = isCompleted(), isCanceled = isCanceled()) }
//
//        val result = sut.await(client)
//
//        coVerify(exactly = 2) {
//            client.getPrediction<String>(predictionId)
//        }
//
//        assertEquals(PredictionStatus.FAILED, result?.status)
//    }
//
//    @Test
//    fun `Given a canceled prediction, When await is called, Then a failure status is returned`() = runTest {
//        val predictionId = "xyzRTfj56"
//        val predictionProcessing = Prediction<String>(id = predictionId, status = PredictionStatus.PROCESSING)
//        val predictionSucceeded = Prediction<String>(id = predictionId, status = PredictionStatus.FAILED)
//        val client = mockk<Replicate>()
//
//        coEvery { client.getPrediction<String>(predictionId) } returnsMany listOf(
//            Task.success(predictionProcessing, isComplete = false, isCanceled = false),
//            Task.success(predictionSucceeded, isComplete = true, isCanceled = false)
//        )
//
//        val predictionStarting = Prediction<String>(id = predictionId, status = PredictionStatus.CANCELED)
//        val sut = with(predictionStarting) { Task.success(this, isComplete = isCompleted(), isCanceled = isCanceled()) }
//
//
//
//        assertThrows<CancellationException> {
//            runBlocking { sut.await(client) }
//        }
//
//        coVerify(exactly = 0) {
//            client.getPrediction<String>(predictionId)
//        }
//    }
}
