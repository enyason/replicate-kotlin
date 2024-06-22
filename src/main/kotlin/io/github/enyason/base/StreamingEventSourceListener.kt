package io.github.enyason.base

import io.github.enyason.predictions.PredictionsApi
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class StreamingEventSourceListener(val onEvent: (String) -> Unit, val onError: (String) -> Unit) : EventSourceListener() {
    override fun onEvent(
        eventSource: EventSource,
        id: String?,
        type: String?,
        data: String,
    ) {
        super.onEvent(eventSource, id, type, data)
        when (EventType.getType(type)) {
            EventType.OUTPUT -> onEvent(data)
            EventType.DONE -> onDone(data, onEvent)
            EventType.ERROR -> onError(data, onError)
            EventType.UNKNOWN -> onError("Unknown Event type received")
        }
    }

    override fun onFailure(
        eventSource: EventSource,
        t: Throwable?,
        response: Response?,
    ) {
        super.onFailure(eventSource, t, response)
        t?.run { throw this }
    }

    private fun onDone(
        data: String,
        onEvent: (String) -> Unit,
    ) {
        if (data.isEmptyJson()) {
            return
        }

        val done = PredictionsApi.gson.fromJson(data, Done::class.java)
        if (done.reason.isError()) {
            return
        }
        onEvent(done.reason)
    }

    private fun onError(
        data: String,
        onError: (String) -> Unit,
    ) {
        val error = PredictionsApi.gson.fromJson(data, Error::class.java)
        onError(error.detail)
    }

    private fun String.isEmptyJson() = equals("{}")

    private fun String.isError() = equals("error", true)

    enum class EventType {
        OUTPUT,
        DONE,
        ERROR,
        UNKNOWN,
        ;

        companion object {
            fun getType(type: String?): EventType {
                return type?.let { eType ->
                    entries.find { eType.uppercase() == it.name }
                } ?: UNKNOWN
            }
        }
    }

    data class Done(val reason: String)

    data class Error(val detail: String)
}
