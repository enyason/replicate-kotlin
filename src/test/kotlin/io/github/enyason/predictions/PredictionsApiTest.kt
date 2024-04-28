package io.github.enyason.predictions

import com.google.gson.Gson
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.client.TestPredictable
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.predictions.models.PredictionDTO
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PredictionsApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sut: PredictionsApi
    private val gson = Gson()

    @BeforeTest
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
        sut = PredictionsApi(ReplicateConfig("tokenYUIOPJHIGUFYDRT", baseUrl = mockWebServer.url("").toString()))
    }

    @AfterTest
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test createPrediction _API returns success response`() = runTest {
        val predictable = TestPredictable(versionId = "2exbc4", input = mapOf("prompt" to "hd image of Einstein"))
        val requestBody = mapOf(
            "version" to predictable.versionId,
            "input" to predictable.input
        )

        val responseBody = PredictionDTO(
            id = "random-id",
            model = "some-model",
            version = "2exbc4",
            input = mapOf("prompt" to "hd image of Einstein"),
            output = listOf("outputUrl"),
            status = "succeeded"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(responseBody))
        )

        val response = sut.createPrediction(requestBody)
        val request = mockWebServer.takeRequest()

        assertEquals(responseBody.toPrediction(), response.first)
        assertNull(response.second)
        assertEquals("POST", request.method)
        assertEquals("/predictions", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test createPrediction _API returns error response _exception is thrown`() = runTest {
        val predictable = TestPredictable(versionId = "2exbc4", input = mapOf("prompt" to "hd image of Einstein"))
        val requestBody = mapOf(
            "version" to predictable.versionId,
            "input" to predictable.input
        )

        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )

        val response = sut.createPrediction(requestBody)
        val request = mockWebServer.takeRequest()

        assertNull(response.first)
        assertTrue(response.second is IllegalStateException)
        assertEquals("POST", request.method)
        assertEquals("/predictions", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }
}
