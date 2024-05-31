package io.github.enyason.base

import okhttp3.logging.HttpLoggingInterceptor

/**
 * A set of configurations used to create the Replicate Client, these configurations ultimately control the way the SDK behaves
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
data class ReplicateConfig(
    /**
     * This is the access token for interacting with replicate API
     */
    val apiToken: String,

    /**
     * API url for replicate
     */
    val baseUrl: String = "https://api.replicate.com/v1/",

    /**
     * Specifies if network request/response should be printed to console
     */
    val enableLogging: Boolean = true,

    /**
     * Specifies the kind of information from the request/response to print to console
     */
    val loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,

    /**
     * Specifies the time delay in milliseconds for polling the server to get the completed state of
     * a [io.github.enyason.domain.models.Prediction]
     */
    val pollingDelayInMillis: Long = 2000
)
