package io.github.enyason.predictions.models

data class PaginatedPredictionsDTO(
    val next: String?,
    val previous: String?,
    val results: List<PredictionDTO<Any>>,
)
