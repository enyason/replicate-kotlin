package io.github.enyason.client

import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictable.Predictable
import io.github.enyason.predictions.PredictionsApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

data class TestPredictable(
    override val modelId: String? = null,
    override val versionId: String,
    override val input: Map<String, Any>
) : Predictable

class ReplicateTest {

    private lateinit var predictionApi: PredictionsApi
    private lateinit var sut: Replicate

    @BeforeTest
    fun before() {
        predictionApi = mockk()
        sut = Replicate(predictionAPI = predictionApi)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given invalid arguments, When creating a prediction, Then throw an exception`() = runTest {
        val predictable = TestPredictable(versionId = "", input = emptyMap())
        val result = sut.createPrediction<Any>(predictable)
        assertTrue { result.exception is IllegalArgumentException }
    }

    @Test
    fun `When creating a prediction fails, Then return an error result`() = runTest {
        val predictable =
            TestPredictable(versionId = "2exbc4", input = mapOf("prompt" to "hd image of Einstein"))
        val errorMessage = "Could not create a prediction, try again"
        coEvery { predictionApi.createPrediction<Any>(any(), any()) } returns Pair(
            null,
            IllegalStateException(errorMessage)
        )

        val result = sut.createPrediction<Any>(predictable)

        assertFalse { result.isSuccessful }
        assertTrue { result.exception?.message == errorMessage }
    }

    //
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
        coEvery { predictionApi.createPrediction<List<String>>(any(), any()) } returns Pair(prediction, null)
        every { predictionApi.pollingDelayInMillis } returns 2000L

        val result = sut.createPrediction<List<String>>(predictable)

        assertTrue { result.isSuccessful }
        assertTrue { result.result == prediction }
    }

    //
    @Test
    fun `Given empty prediction Id, When getting a prediction, Then throw an exception`() =
        runTest {
            val result = sut.getPrediction<Any>(" ")
            assertTrue { result.exception is IllegalArgumentException }
        }

    @Test
    fun `When getting a prediction fails, Then return an error result`() = runTest {
        val predictionId = "sWeZFZou6v3CPKuoJbqX46ugPaHT1DcsWYx0srPmGrMOCPYI"
        val errorMessage = "Could not fetch prediction"

        coEvery { predictionApi.getPrediction<Any>(predictionId, any()) } returns Pair(
            null,
            IllegalStateException(errorMessage)
        )
        val result = sut.getPrediction<Any>(predictionId)

        assertFalse { result.isSuccessful }
        assertTrue { result.exception?.message == errorMessage }
    }

    //
    @Test
    fun `When getting a prediction succeeds, Then return an success result`() = runTest {
        val predictionId = "ac732df83cea7fff18b8472768c88ad041fa750ff7682a21affe81863cbe77e4"
        val predictionOutputUrl =
            "https://replicate.delivery/pbxt/sWeZFZou6v3CPKuoJbqX46ugPaHT1DcsWYx0srPmGrMOCPYIA/out-0.png"

        val prediction = Prediction(
            id = predictionId,
            output = listOf<Any>(predictionOutputUrl)
        )
        coEvery { predictionApi.getPrediction<List<Any>>(predictionId, any()) } returns Pair(prediction, null)
        every { predictionApi.pollingDelayInMillis } returns 2000L

        val result = sut.getPrediction<Any>(predictionId)

        assertTrue { result.isSuccessful }
        assertTrue { result.result == prediction }
    }

//
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
        val errorMessage = "Could not cancel prediction with ID: $predictionId"
        coEvery { predictionApi.cancelPrediction(any()) } returns Pair(false, IllegalStateException(errorMessage))

        val result = sut.cancelPrediction(predictionId)

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        assertNull(result.getOrNull())
    }
}
