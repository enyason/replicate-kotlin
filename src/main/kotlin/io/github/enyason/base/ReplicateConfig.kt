package io.github.enyason.base

data class ReplicateConfig(
    val apiToken: String,
    val baseUrl: String = "https://api.replicate.com/v1/",
    val enableLogging: Boolean = true
)
