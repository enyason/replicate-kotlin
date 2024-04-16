package io.github.enyason.singleentry.predictions

import io.github.enyason.singleentry.ReplicateApi
import retrofit2.Response

// Placeholder extension function
suspend fun ReplicateApi.createPrediction(): Response<Any?> {
    return predictionsApiService.createPrediction()
}
