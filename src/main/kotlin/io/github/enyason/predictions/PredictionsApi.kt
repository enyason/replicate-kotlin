package io.github.enyason.predictions

import io.github.enyason.base.ReplicateConfig
import io.github.enyason.base.RetrofitFactory
import io.github.enyason.domain.mappers.toPrediction
import io.github.enyason.domain.models.Prediction
import io.github.enyason.io.github.enyason.predictions.models.toModel
import io.github.enyason.predictions.models.PredictionDTO
import retrofit2.Response

class PredictionsApi(config: ReplicateConfig) {

    constructor(apiToken: String) : this(ReplicateConfig(apiToken = apiToken))

    private val retrofit by lazy { RetrofitFactory.buildRetrofit(config) }

    internal val service by lazy { retrofit.create(PredictionsApiService::class.java) }
}

// Placeholder extension function
suspend fun PredictionsApi.createPrediction(requestBody: Map<String, Any>): Prediction {
    val response = service.createPrediction(requestBody)
    val predictionDto = response.body()
    if (response.isSuccessful && predictionDto != null) {
        return predictionDto.toPrediction()
    } else {
        val error = response.errorBody()?.toModel()
        val message = error?.detail ?: "Could not create predication"
        throw IllegalStateException(message)
    }
}
