package com.arq.currencyconverter.feature.converter.di

import com.arq.currencyconverter.feature.converter.data.ConverterRepositoryImpl
import com.arq.currencyconverter.feature.converter.data.api.ConverterApi
import com.arq.currencyconverter.feature.converter.data.service.ConverterService
import com.arq.currencyconverter.feature.converter.data.service.ConverterServiceImpl
import com.arq.currencyconverter.feature.converter.domain.repository.ConverterRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConverterModule {

    @Binds
    @Singleton
    abstract fun bindConverterService(impl: ConverterServiceImpl): ConverterService

    @Binds
    @Singleton
    abstract fun bindConverterRepository(impl: ConverterRepositoryImpl): ConverterRepository

    companion object {
        @Provides
        @Singleton
        fun provideConverterApi(retrofit: Retrofit): ConverterApi =
            retrofit.create(ConverterApi::class.java)
    }
}
