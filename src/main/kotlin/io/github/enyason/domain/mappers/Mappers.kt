package io.github.enyason.domain.mappers

import io.github.enyason.domain.models.Metrics
import io.github.enyason.domain.models.Prediction
import io.github.enyason.domain.models.PredictionStatus
import io.github.enyason.domain.models.Urls
import io.github.enyason.predictions.models.MetricsDTO
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.UrlsDTO

fun PredictionDTO.toPrediction(): Prediction {
    return Prediction(
        id = this.id,
        model = this.model,
        version = this.version,
        input = this.input,
        logs = this.logs,
        output = this.output,
        error = this.error,
        status = PredictionStatus.getStatus(this.status),
        source = this.source,
        createdAt = this.createdAt,
        startedAt = this.startedAt,
        completedAt = this.completedAt,
        metrics = this.metrics.toMetrics(),
        urls = this.urls.toUrls()
    )
}

fun MetricsDTO?.toMetrics(): Metrics? {
    return this?.let {
        Metrics(
            predictTime = it.predictTime,
            totalTime = it.totalTime
        )
    }
}

fun UrlsDTO?.toUrls(): Urls? {
    return this?.let {
        Urls(
            cancel = it.cancel,
            get = it.get,
            stream = it.stream
        )
    }
}
