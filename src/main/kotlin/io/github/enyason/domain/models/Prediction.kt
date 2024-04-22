package io.github.enyason.domain.models

data class Prediction(
    var id: String? = null,
    var model: String? = null,
    var version: String? = null,
    var input: Map<String, Any?>? = null,
    var logs: String? = null,
    var output: Any? = null,
    var error: String? = null,
    var status: PredictionStatus? = null,
    var source: String? = null,
    var createdAt: String? = null,
    var startedAt: String? = null,
    var completedAt: String? = null,
    var metrics: Metrics? = null,
    var urls: Urls? = null
)
