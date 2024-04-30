package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

data class TestPredictable(
    override val modelId: String? = null,
    override val versionId: String,
    override val input: Map<String, Any>
) : Predictable

class ReplicateClientTest {

    private lateinit var predictionApi: PredictionsApi
    private lateinit var sut: ReplicateClient

    @BeforeTest
    fun before() {
        predictionApi = mockk()
        sut = ReplicateClient(predictionAPI = predictionApi)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
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

    @Test
    fun `test cancelPrediction _Empty predictionId passed _Exception is thrown`() = runTest {
        val result = sut.cancelPrediction("")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(result.isFailure)
        assertEquals("Provided an empty prediction ID", result.exceptionOrNull()?.message)
    }

    @Test
    fun `test cancelPrediction _Call to API is successful _Result is success`() = runTest {
        coEvery { predictionApi.cancelPrediction(any()) } returns Pair(true, null)

        val result = sut.cancelPrediction("pId")

        assertTrue(result.isSuccess)
        assertNull(result.exceptionOrNull())
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `test cancelPrediction _Call to API fails _Result is failure`() = runTest {
        val predictionId = "pId"
        val errorMessage = "Could not cancel predication with ID: $predictionId"
        coEvery { predictionApi.cancelPrediction(any()) } returns Pair(false, IllegalStateException(errorMessage))

        val result = sut.cancelPrediction(predictionId)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        assertNull(result.getOrNull())
    }
}
