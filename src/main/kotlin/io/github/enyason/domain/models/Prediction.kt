package io.github.enyason.domain.models

/**
 * A [Prediction] is the result from running an AI model on [Replicate](https://replicate.com)
 *
 * [output] is a generic type because the shape of the data return from running the AI models differs
 * from each other
 */
data class Prediction<T>(
    var id: String? = null,
    var model: String? = null,
    var version: String? = null,
    var input: Map<String, Any?>? = null,
    var logs: String? = null,
    var output: T? = null,
    var error: String? = null,
    var status: PredictionStatus? = null,
    var source: String? = null,
    var createdAt: String? = null,
    var startedAt: String? = null,
    var completedAt: String? = null,
    var metrics: Metrics? = null,
    var urls: Urls? = null
)


fun <T> Prediction<T>.isCompleted(): Boolean {
    return status == PredictionStatus.SUCCEEDED
}

fun <T> Prediction<T>.isCanceled(): Boolean {
    return status == PredictionStatus.CANCELED
}
