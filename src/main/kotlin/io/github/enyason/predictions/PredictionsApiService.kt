package io.github.enyason.predictions

import io.github.enyason.predictions.models.PredictionDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * The main interface for communicating with [Replicate's](https://replicate.com) Predictions API
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 * @author Emmanuel Enya <a href="https://github.com/enyason">link</a>
 */
interface PredictionsApiService {

    companion object {
        const val ENDPOINT = "predictions"
    }

    @POST(ENDPOINT)
    @JvmSuppressWildcards
    suspend fun createPrediction(@Body predictionRequest: Map<String, Any>): Response<PredictionDTO>
}
