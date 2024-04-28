package io.github.enyason.base

import okhttp3.logging.HttpLoggingInterceptor

/**
 * A set of configurations used to create the Replicate Client, these configurations ultimately control the way the SDK behaves
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
data class ReplicateConfig(
    val apiToken: String,
    val baseUrl: String = "https://api.replicate.com/v1/",
    val enableLogging: Boolean = true,
    val loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
)
