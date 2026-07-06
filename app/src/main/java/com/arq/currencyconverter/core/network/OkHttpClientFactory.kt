package com.arq.currencyconverter.core.network

import com.arq.currencyconverter.core.network.interceptor.GlobalResponseInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientFactory {
    private const val TIMEOUT = 10L

    fun create(chuckerInterceptor: ChuckerInterceptor, isDebug: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(GlobalResponseInterceptor())
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)

        if (isDebug) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }
        return builder.build()
    }
}
