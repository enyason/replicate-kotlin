package io.github.enyason.base

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A [Retrofit] builder which provides a single instance of Retrofit throughout the Application lifecycle
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
internal object RetrofitFactory {

    private lateinit var retrofit: Retrofit

    /**
     * Returns a new or existing instance of [Retrofit].
     * The new [Retrofit] instance is built using configurations specified in [ReplicateConfig]
     */
    fun buildRetrofit(
        config: ReplicateConfig,
        loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
    ): Retrofit {
        if (this::retrofit.isInitialized) {
            return retrofit
        }

        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(buildHttpClient(config, loggingLevel))
            .addConverterFactory(createConverterFactory())
            .build()
        return retrofit
    }

    private fun buildHttpClient(
        config: ReplicateConfig,
        loggingLevel: HttpLoggingInterceptor.Level
    ) = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${config.apiToken}")
                .build()
            chain.proceed(request)
        }.also {
            if (config.enableLogging) it.addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
        }.build()

    private fun createConverterFactory() = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )
}
