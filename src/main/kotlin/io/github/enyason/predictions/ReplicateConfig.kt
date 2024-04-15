package io.github.enyason.predictions

data class ReplicateConfig(
    val apiToken: String,
    val baseUrl: String = "https://api.replicate.com/v1/",
    val enableLogging: Boolean = true
)
