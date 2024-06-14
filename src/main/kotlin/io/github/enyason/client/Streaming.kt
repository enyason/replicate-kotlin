package io.github.enyason.client

import io.github.enyason.base.StreamingEventSourceListener
import io.github.enyason.domain.models.Prediction
import io.github.enyason.predictions.PredictionsApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.sse.EventSources

fun Prediction<Any>.stream() = callbackFlow {
    EventSources
        .createFactory(PredictionsApi.sseClient())
        .newEventSource(
            request = PredictionsApi.sseRequest(
                urls?.stream ?: throw Exception("Stream URL is null.")
            ),
            listener = StreamingEventSourceListener { data -> trySend(data) }
        )

    awaitClose()
}
