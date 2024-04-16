package io.github.enyason.singleentry

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.multipleentry.predictions.PredictionsApiService
import io.github.enyason.multipleentry.trainings.TrainingsApiService

class ReplicateApi(config: ReplicateConfig) {

    constructor(apiToken: String) : this(ReplicateConfig(apiToken = apiToken))

    private val retrofit by lazy { RetrofitFactory.buildRetrofit(config) }

    internal val predictionsApiService by lazy { retrofit.create(PredictionsApiService::class.java) }

    internal val trainingsApiService by lazy { retrofit.create(TrainingsApiService::class.java) }

    // Other ApiServices will come here
}
