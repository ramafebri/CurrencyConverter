package com.arq.currencyconverter.di

import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatterImpl
import com.arq.currencyconverter.core.dateformatter.DateFormatter
import com.arq.currencyconverter.core.dateformatter.DateFormatterImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Provides
    @Singleton
    fun provideCurrencyFormatter(): CurrencyFormatter = CurrencyFormatterImpl()

    @Provides
    @Singleton
    fun provideDateFormatter(): DateFormatter = DateFormatterImpl()
}
