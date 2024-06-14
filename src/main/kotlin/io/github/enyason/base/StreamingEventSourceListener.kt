package io.github.enyason.base

import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class StreamingEventSourceListener(val onEvent: (String) -> Unit, val onError: () -> Unit) : EventSourceListener() {
    override fun onEvent(
        eventSource: EventSource,
        id: String?,
        type: String?,
        data: String
    ) {
        super.onEvent(eventSource, id, type, data)
        when (EventType.getType(type)) {
            EventType.OUTPUT -> onEvent(data)
            EventType.DONE -> {}
            EventType.ERROR, EventType.UNKNOWN -> onError()
        }
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        super.onFailure(eventSource, t, response)
        t?.run { throw this }
    }

    enum class EventType {
        OUTPUT, DONE, ERROR, UNKNOWN;

        companion object {
            fun getType(type: String?): EventType {
                return type?.let { eType ->
                    entries.find { eType.uppercase() == it.name }
                } ?: UNKNOWN
            }
        }
    }
}
