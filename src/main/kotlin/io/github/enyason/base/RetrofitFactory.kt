package io.github.enyason.base

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private lateinit var retrofit: Retrofit

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
