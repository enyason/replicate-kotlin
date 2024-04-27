package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertTrue

data class TestPredictable(
    override val modelId: String? = null,
    override val versionId: String,
    override val input: Map<String, Any>
) : Predictable

class ReplicateClientTest {

    private lateinit var predictionApi: PredictionsApi
    private lateinit var sut: ReplicateClient

    @BeforeEach
    fun before() {
        predictionApi = mockk<PredictionsApi>()
        sut = ReplicateClient(predictionAPI = predictionApi)
    }

    @Test
    fun `Given invalid arguments, When creating a prediction, Then throw an exception`() = runTest {
        val predictable = TestPredictable(versionId = "", input = emptyMap())
        val result = sut.createPrediction(predictable)
        assertTrue { result.exceptionOrNull() is IllegalArgumentException }
    }

    @Test
    fun `When creating a prediction fails, Then return an error result`() = runTest {
        val predictable = TestPredictable(versionId = "2exbc4", input = mapOf("prompt" to "hd image of Einstein"))
        val errorMessage = "Could not create a prediction, try again"
        coEvery { predictionApi.createPrediction(any()) } returns Pair(null, IllegalStateException(errorMessage))

        val result = sut.createPrediction(predictable)

        assertTrue { result.isFailure }
        assertTrue { result.exceptionOrNull()?.message == errorMessage }
    }

    @Test
    fun `When creating a prediction succeeds, Then return an success result`() = runTest {
        val predictable = TestPredictable(
            versionId = "ac732df83cea7fff18b8472768c88ad041fa750ff7682a21affe81863cbe77e4",
            input = mapOf("prompt" to "hd image of Einstein")
        )

        val predictionOutputUrl =
            "https://replicate.delivery/pbxt/sWeZFZou6v3CPKuoJbqX46ugPaHT1DcsWYx0srPmGrMOCPYIA/out-0.png"

        val prediction = Prediction(
            id = "random-id",
            output = listOf(predictionOutputUrl)
        )
        coEvery { predictionApi.createPrediction(any()) } returns Pair(prediction, null)

        val result = sut.createPrediction(predictable)

        assertTrue { result.isSuccess }
        assertTrue { result.getOrNull() == prediction }
    }
}
