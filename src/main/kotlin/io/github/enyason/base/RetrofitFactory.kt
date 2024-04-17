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

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${config.apiToken}")
                    .build()
                chain.proceed(request)
            }.also {
                if (config.enableLogging) it.addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
            }
        val gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit
    }
}
