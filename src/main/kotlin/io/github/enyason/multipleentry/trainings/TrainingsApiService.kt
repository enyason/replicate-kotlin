package io.github.enyason.multipleentry.trainings

import retrofit2.Response
import retrofit2.http.POST

interface TrainingsApiService {

    companion object {
        const val ENDPOINT = "trainings"
    }

    // PlaceHolder method
    @POST(ENDPOINT)
    suspend fun createTraining(): Response<Any?>
}
