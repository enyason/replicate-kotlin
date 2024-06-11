package io.github.enyason.predictions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.enyason.base.ReplicateConfig
import io.github.enyason.client.TestPredictable
import io.github.enyason.domain.predictions.models.PaginatedPredictions
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.toPaginatedPredictions
import io.github.enyason.domain.predictions.toPrediction
import io.github.enyason.predictions.models.PaginatedPredictionsDTO
import io.github.enyason.predictions.models.PredictionDTO
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.HttpException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PredictionsApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var sut: PredictionsApi
    private val gson = Gson()
    private lateinit var id: String
    private lateinit var version: String
    private lateinit var input: Map<String, Any>
    private val objectType = object : TypeToken<PredictionDTO<List<String>>>() {}.type

    @BeforeTest
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
        sut = PredictionsApi(
            ReplicateConfig(
                "tokenYUIOPJHIGUFYDRT",
                baseUrl = mockWebServer.url(path = "").toString()
            )
        )
        id = "random-id"
        version = "2exbc4"
        input = mapOf("prompt" to "hd image of Einstein")
    }

    @AfterTest
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test createPrediction _API returns success response`() = runTest {
        val requestBody = buildRequestBody()

        val responseBody = PredictionDTO(
            id = id,
            model = "some-model",
            version = version,
            input = input,
            output = listOf("outputUrl"),
            status = "succeeded"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(responseBody))
        )

        val response: Pair<Prediction<List<String>>?, Exception?> =
            sut.createPrediction(requestBody, objectType)
        val request = mockWebServer.takeRequest()

        assertEquals(responseBody.toPrediction(), response.first)
        assertNull(response.second)
        assertEquals("POST", request.method)
        assertEquals("/predictions", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test createPrediction _API returns error response _exception is thrown`() = runTest {
        val requestBody = buildRequestBody()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )

        val response: Pair<Prediction<List<String>>?, Exception?> =
            sut.createPrediction(requestBody, objectType)
        val request = mockWebServer.takeRequest()

        assertNull(response.first)
        assertTrue(response.second is HttpException)
        assertEquals("POST", request.method)
        assertEquals("/predictions", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test getPrediction _API returns success response`() = runTest {
        val responseBody = PredictionDTO(
            id = id,
            model = "some-model",
            version = version,
            input = input,
            output = listOf("outputUrl"),
            status = "succeeded"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(responseBody))
        )

        val response: Pair<Prediction<List<String>>?, Exception?> =
            sut.getPrediction(id, objectType)
        val request = mockWebServer.takeRequest()

        assertEquals(responseBody.toPrediction(), response.first)
        assertNull(response.second)
        assertEquals("GET", request.method)
        assertEquals("/predictions/$id", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test getPrediction _API returns error response _exception is thrown`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(401)
        )

        val response: Pair<Prediction<List<String>>?, Exception?> =
            sut.getPrediction(id, objectType)
        val request = mockWebServer.takeRequest()

        assertNull(response.first)
        assertTrue(response.second is HttpException)
        assertEquals("GET", request.method)
        assertEquals("/predictions/$id", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test cancelPrediction _API returns success response`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        val response = sut.cancelPrediction(id)
        val request = mockWebServer.takeRequest()

        assertTrue(response.first)
        assertNull(response.second)
        assertEquals("POST", request.method)
        assertEquals("/predictions/$id/cancel", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test cancelPrediction _API returns error response _exception is thrown`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(500)
        )

        val response = sut.cancelPrediction(id)
        val request = mockWebServer.takeRequest()

        assertFalse(response.first)
        assertTrue(response.second is IllegalStateException)
        assertEquals("POST", request.method)
        assertEquals("/predictions/$id/cancel", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test listPredictions _API returns one-page success response`() = runTest {
        val responseBody = PaginatedPredictionsDTO(
            next = null,
            previous = null,
            results = listOf(
                PredictionDTO(
                    id = id,
                    model = "some-model",
                    version = version,
                    input = input,
                    output = listOf("outputUrl"),
                    status = "succeeded"
                )
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(responseBody))
        )

        val response: Pair<PaginatedPredictions?, Exception?> =
            sut.listPredictions("")
        val request = mockWebServer.takeRequest()
        val predictions = response.first

        assertEquals(responseBody.toPaginatedPredictions(), predictions)
        assertNotNull(predictions)
        assertFalse(predictions.hasNext())
        assertFalse(predictions.hasPrevious())
        assertNull(response.second)
        assertEquals("GET", request.method)
        assertEquals("/predictions?cursor=", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test listPredictions with cursor _API returns success with next and previous cursor response`() = runTest {
        val nextCursor = "third_page"
        val previousCursor = "first_page"
        val responseBody = PaginatedPredictionsDTO(
            next = "https://api.replicate.com/v1/predictions?cursor=$nextCursor",
            previous = "https://api.replicate.com/v1/predictions?cursor=$previousCursor",
            results = listOf(
                PredictionDTO(
                    id = id,
                    model = "some-model",
                    version = version,
                    input = input,
                    output = listOf("outputUrl"),
                    status = "succeeded"
                )
            )
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(responseBody))
        )
        val cursor = "second_page"

        val response: Pair<PaginatedPredictions?, Exception?> =
            sut.listPredictions(cursor)
        val request = mockWebServer.takeRequest()
        val paginatedPredictions = response.first

        assertEquals(responseBody.toPaginatedPredictions(), paginatedPredictions)
        assertNotNull(paginatedPredictions)
        assertTrue(paginatedPredictions.hasNext())
        assertTrue(paginatedPredictions.hasPrevious())
        assertEquals(nextCursor, paginatedPredictions.next)
        assertEquals(previousCursor, paginatedPredictions.previous)
        assertNull(response.second)
        assertEquals("GET", request.method)
        assertEquals("/predictions?cursor=$cursor", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    @Test
    fun `test listPredictions _API returns error response _exception is thrown`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val response: Pair<PaginatedPredictions?, Exception?> =
            sut.listPredictions("")
        val request = mockWebServer.takeRequest()
        val paginatedPredictions = response.first

        assertNull(paginatedPredictions)
        assertNotNull(response.second)
        assertEquals("GET", request.method)
        assertEquals("/predictions?cursor=", request.path)
        assertTrue(request.headers["Authorization"] != null)
    }

    private fun buildRequestBody(): Map<String, Any> {
        val predictable = TestPredictable(versionId = version, input = input)
        return mapOf(
            "version" to predictable.versionId,
            "input" to predictable.input
        )
    }
}
