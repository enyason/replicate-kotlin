package io.github.enyason.predictions

import retrofit2.Response

class PredictionsApi(config: ReplicateConfig) {

    constructor(apiToken: String) : this(ReplicateConfig(apiToken = apiToken))

    private val retrofit by lazy { RetrofitFactory.buildRetrofit(config) }

    internal val service by lazy { retrofit.create(PredictionsApiService::class.java) }
}

// Placeholder extension function
suspend fun PredictionsApi.createPrediction(): Response<Any?> {
    return service.createPrediction()
}