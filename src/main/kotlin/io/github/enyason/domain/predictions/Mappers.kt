package io.github.enyason.domain.predictions

import io.github.enyason.domain.predictions.models.Metrics
import io.github.enyason.domain.predictions.models.PaginatedPredictions
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.domain.predictions.models.PredictionStatus
import io.github.enyason.domain.predictions.models.Urls
import io.github.enyason.predictions.models.MetricsDTO
import io.github.enyason.predictions.models.PaginatedPredictionsDTO
import io.github.enyason.predictions.models.PredictionDTO
import io.github.enyason.predictions.models.UrlsDTO

fun <OUTPUT> PredictionDTO<OUTPUT>.toPrediction(): Prediction<OUTPUT> {
    return Prediction(
        id = this.id.orEmpty(),
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
        urls = this.urls.toUrls(),
    )
}

fun PaginatedPredictionsDTO.toPaginatedPredictions(): PaginatedPredictions {
    return PaginatedPredictions(
        next = this.next,
        previous = this.previous,
        results = this.results.map { it.toPrediction() },
    )
}

fun MetricsDTO?.toMetrics(): Metrics? {
    return this?.let {
        Metrics(
            predictTime = it.predictTime,
            totalTime = it.totalTime,
        )
    }
}

fun UrlsDTO?.toUrls(): Urls? {
    return this?.let {
        Urls(
            cancel = it.cancel,
            get = it.get,
            stream = it.stream,
        )
    }
}
