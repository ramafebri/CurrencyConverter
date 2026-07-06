package com.arq.currencyconverter.di

import android.content.Context
import android.os.StrictMode
import com.arq.currencyconverter.BuildConfig
import com.arq.currencyconverter.core.network.NetworkConstants
import com.arq.currencyconverter.core.network.OkHttpClientFactory
import com.arq.currencyconverter.core.network.RetrofitFactory
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        val oldPolicy = StrictMode.getThreadPolicy()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder(oldPolicy)
                .permitDiskReads()
                .permitDiskWrites()
                .build()
        )
        val interceptor = ChuckerInterceptor.Builder(context).build()
        StrictMode.setThreadPolicy(oldPolicy)
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient =
        OkHttpClientFactory.create(
            chuckerInterceptor = chuckerInterceptor,
            isDebug = BuildConfig.DEBUG
        )

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = RetrofitFactory.create(
        baseUrl = NetworkConstants.BASE_URL,
        okHttpClient = okHttpClient,
        gson = gson
    )
}
