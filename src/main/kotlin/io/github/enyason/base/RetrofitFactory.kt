package io.github.enyason.base

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.TestOnly
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A [Retrofit] builder which provides a single instance of Retrofit throughout the Application lifecycle
 *
 * @author Joseph Olugbohunmi <a href="https://github.com/mayorJAY">link</a>
 */
internal object RetrofitFactory {

    private var retrofit: Retrofit? = null

    /**
     * Returns a new or existing instance of [Retrofit].
     * The new [Retrofit] instance is built using configurations specified in [ReplicateConfig]
     * @param config A set of configurations used to control the way the SDK behaves
     */
    fun buildRetrofit(
        config: ReplicateConfig
    ): Retrofit {
        if (retrofit != null) {
            return retrofit!!
        }

        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(buildHttpClient(config))
            .addConverterFactory(createConverterFactory())
            .build()
        return retrofit!!
    }

    private fun buildHttpClient(
        config: ReplicateConfig
    ) = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${config.apiToken}")
                .build()
            chain.proceed(request)
        }.also {
            if (config.enableLogging) it.addInterceptor(HttpLoggingInterceptor().setLevel(config.loggingLevel))
        }.build()

    private fun createConverterFactory() = GsonConverterFactory.create(
        GsonBuilder()
            .setLenient()
            .create()
    )

    @TestOnly
    internal fun reset() {
        retrofit = null
    }
}
