package io.github.enyason.predictions

import await
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus
import io.github.enyason.domain.models.isCompleted
import io.github.enyason.io.github.enyason.predictions.DefaultTask
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class DefaultTaskTest {

    @Test
    fun `test prediction is polled with success prediction status`() = runTest {
        val predictionId = "xyzRTfj56"
        val predictionStarting = Prediction(id = predictionId, status = PredictionStatus.STARTING)
        val predictionProcessing = Prediction(id = predictionId, status = PredictionStatus.PROCESSING)
        val predictionSucceeded = Prediction(id = predictionId, status = PredictionStatus.SUCCEEDED)
        val predictionsApi = mockk<PredictionsApi>()

        coEvery { predictionsApi.getPrediction(predictionId) } returnsMany listOf(
            Pair(predictionProcessing, null),
            Pair(predictionSucceeded, null)
        )

        val sut = DefaultTask.success(predictionStarting, predictionsApi)

        val result = sut.await(delayInMillis = 0)

        coVerify(exactly = 2) {
            predictionsApi.getPrediction(predictionId)
        }

        assertTrue { result.isCompleted() }
    }
}
