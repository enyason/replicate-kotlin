package io.github.enyason.base

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.assertThrows
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RetrofitFactoryTest {

    @AfterTest
    fun tearDown() {
        RetrofitFactory.reset()
    }

    @Test
    fun `test buildRetrofit _all parameters are valid`() {
        val retrofit = RetrofitFactory.buildRetrofit(ReplicateConfig("token"))

        assertNotNull(retrofit.baseUrl())
        assertTrue(retrofit.converterFactories().isNotEmpty())
        assertTrue { retrofit.converterFactories().firstOrNull { it is GsonConverterFactory } != null }
        assertTrue((retrofit.callFactory() as OkHttpClient).interceptors.isNotEmpty())
    }

    @Test
    fun `test buildRetrofit _logging is disabled _HttpLoggingInterceptor is not added`() {
        val retrofit = RetrofitFactory.buildRetrofit(ReplicateConfig("token", enableLogging = false))

        assertEquals(1, (retrofit.callFactory() as OkHttpClient).interceptors.size)
        assertTrue { (retrofit.callFactory() as OkHttpClient).interceptors.firstOrNull { it is HttpLoggingInterceptor } == null }
    }

    @Test
    fun `test buildRetrofit _baseUrl is not provided _IllegalArgumentException is thrown`() {
        assertThrows<IllegalArgumentException>("Should throw an Exception") {
            RetrofitFactory.buildRetrofit(ReplicateConfig("token", baseUrl = ""))
        }
    }
}
