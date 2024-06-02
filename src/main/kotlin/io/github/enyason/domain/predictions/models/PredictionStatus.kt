package io.github.enyason.domain.predictions.models

enum class PredictionStatus {
    STARTING,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELED,
    UNKNOWN;

    companion object {
        fun getStatus(status: String?): PredictionStatus {
            return status?.let { pStatus ->
                entries.find { pStatus.uppercase() == it.name }
            } ?: UNKNOWN
        }
    }
}
