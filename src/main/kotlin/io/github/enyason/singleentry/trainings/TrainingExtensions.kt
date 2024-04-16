package io.github.enyason.io.github.enyason.singleentry.trainings

import io.github.enyason.singleentry.ReplicateApi
import retrofit2.Response

// Placeholder extension function
suspend fun ReplicateApi.createTraining(): Response<Any?> {
    return trainingsApiService.createTraining()
}
