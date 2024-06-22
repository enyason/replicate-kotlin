package io.github.enyason.domain.predictions.models

/**
 * A [Prediction] is the result from running an AI model on [Replicate](https://replicate.com)
 *
 * [output] is a generic type because the shape of the data return from running the AI models differs
 * from each other
 */
data class Prediction<OUTPUT>(
    var id: String,
    var model: String? = null,
    var version: String? = null,
    var input: Map<String, Any?>? = null,
    var logs: String? = null,
    var output: OUTPUT? = null,
    var error: String? = null,
    var status: PredictionStatus = PredictionStatus.UNKNOWN,
    var source: String? = null,
    var createdAt: String? = null,
    var startedAt: String? = null,
    var completedAt: String? = null,
    var metrics: Metrics? = null,
    var urls: Urls? = null,
)

fun <OUTPUT> Prediction<OUTPUT>.isCompleted(): Boolean {
    return status == PredictionStatus.SUCCEEDED
}

fun <OUTPUT> Prediction<OUTPUT>.isCanceled(): Boolean {
    return status == PredictionStatus.CANCELED
}
