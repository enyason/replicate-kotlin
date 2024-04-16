package io.github.enyason.multipleentry.predictions

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
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
