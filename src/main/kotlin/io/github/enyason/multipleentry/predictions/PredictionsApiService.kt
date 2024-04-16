package io.github.enyason.multipleentry.predictions

import retrofit2.Response
import retrofit2.http.POST

interface PredictionsApiService {

    companion object {
        const val ENDPOINT = "predictions"
    }

    // PlaceHolder method
    @POST(ENDPOINT)
    suspend fun createPrediction(): Response<Any?>
}
