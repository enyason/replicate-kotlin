package io.github.enyason.multipleentry.trainings

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import retrofit2.Response

class TrainingsApi(config: ReplicateConfig) {

    constructor(apiToken: String) : this(ReplicateConfig(apiToken = apiToken))

    private val retrofit by lazy { RetrofitFactory.buildRetrofit(config) }

    internal val service by lazy { retrofit.create(TrainingsApiService::class.java) }
}

// Placeholder extension function
suspend fun TrainingsApi.createTraining(): Response<Any?> {
    return service.createTraining()
}
