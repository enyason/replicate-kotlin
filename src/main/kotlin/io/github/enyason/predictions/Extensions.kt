package io.github.enyason.predictions

import java.net.URI

fun String?.getCursor(): String {
    return if (this.isNullOrBlank()) {
        ""
    } else {
        URI(this).query?.split("&")
            ?.firstOrNull { it.contains("cursor") }?.split("=")?.get(1) ?: ""
    }
}
