package io.github.enyason.base

import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class StreamingEventSourceListener(val onEvent: (String) -> Unit) : EventSourceListener() {
    override fun onEvent(
        eventSource: EventSource,
        id: String?,
        type: String?,
        data: String
    ) {
        super.onEvent(eventSource, id, type, data)
        onEvent(data)
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        super.onFailure(eventSource, t, response)
        t?.run { throw this }
    }
}
