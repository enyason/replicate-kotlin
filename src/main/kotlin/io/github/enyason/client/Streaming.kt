package io.github.enyason.client

import io.github.enyason.base.StreamingEventSourceListener
import io.github.enyason.domain.predictions.models.Prediction
import io.github.enyason.predictions.PredictionsApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.sse.EventSources

/**
 * Stream the output of a Prediction which must have been created
 * using [io.github.enyason.client.Replicate.createPrediction].
 *
 * @return a [Flow] of Strings representing the output produced as server-sent events (SSE).
 */
fun Prediction<Any>.stream() = callbackFlow {
    EventSources
        .createFactory(PredictionsApi.sseClient())
        .newEventSource(
            request = PredictionsApi.sseRequest(
                urls?.stream ?: throw Exception("Stream URL is null.")
            ),
            listener = StreamingEventSourceListener(
                onEvent = { data -> trySend(data) },
                onError = { error(it) }
            )
        )

    awaitClose()
}
