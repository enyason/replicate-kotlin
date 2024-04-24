package io.github.enyason.predictions

import io.github.enyason.predictions.models.PredictionDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PredictionsApiService {

    companion object {
        const val ENDPOINT = "predictions"
    }

    // PlaceHolder method
    @POST(ENDPOINT)
    @JvmSuppressWildcards
    suspend fun createPrediction(@Body predictionRequest: Map<String, Any>): Response<PredictionDTO>
}
