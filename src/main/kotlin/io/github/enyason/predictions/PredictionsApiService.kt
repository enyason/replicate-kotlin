package io.github.enyason.predictions

import io.github.enyason.predictions.models.PredictionDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * The main interface for communicating with [Replicate's](https://replicate.com) Predictions API
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 * @author Love Otudor <a href="https://github.com/Lamouresparus">link</a>
 */
interface PredictionsApiService {

    companion object {
        const val ENDPOINT = "predictions"
    }

    @POST(ENDPOINT)
    @JvmSuppressWildcards
    suspend fun createPrediction(@Body predictionRequest: Map<String, Any>): Response<PredictionDTO>

    @GET("$ENDPOINT/{predictionId}")
    suspend fun getPrediction(@Path("predictionId") predictionId: String): Response<PredictionDTO>

    @POST("$ENDPOINT/{predictionId}/cancel")
    suspend fun cancelPrediction(@Path("predictionId") predictionId: String): Response<Unit>
}
