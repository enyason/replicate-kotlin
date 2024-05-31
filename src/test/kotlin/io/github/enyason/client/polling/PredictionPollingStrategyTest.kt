package io.github.enyason.client.polling

import com.google.gson.reflect.TypeToken
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus
import io.github.enyason.predictions.PredictionsApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PredictionPollingStrategyTest {

    @Test
    fun `Given a Task, When polling is triggered, Then verify Prediction API call count`() = runTest {
        val taskId = "task-id"
        val predictionsApi = mockk<PredictionsApi>()
        val predictionPollingStrategy = PredictionPollingStrategy<Any>(predictionsApi, 0)
        val predictionOutputType = object : TypeToken<Any>() {}.type
        val args = mapOf(PredictionPollingStrategy.PREDICTION_OUTPUT_TYPE_ARG to predictionOutputType)
        val startingPrediction = Prediction<Any>(id = taskId, status = PredictionStatus.STARTING)
        val processingPrediction = Prediction<Any>(id = taskId, status = PredictionStatus.PROCESSING)
        val succeededPrediction = Prediction<Any>(id = taskId, status = PredictionStatus.SUCCEEDED)

        coEvery { predictionsApi.getPrediction<Any>(any(), any()) } returnsMany listOf(
            Pair(startingPrediction, null),
            Pair(processingPrediction, null),
            Pair(succeededPrediction, null)
        )

        predictionPollingStrategy.pollTask(taskId, args)

        coVerify(atLeast = 3) { predictionsApi.getPrediction<Any>(taskId, predictionOutputType) }
    }
}
