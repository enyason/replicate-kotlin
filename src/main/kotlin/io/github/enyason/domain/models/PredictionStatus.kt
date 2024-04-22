package io.github.enyason.domain.models

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
                values().find { pStatus.uppercase() == it.name }
            } ?: UNKNOWN
        }
    }
}
