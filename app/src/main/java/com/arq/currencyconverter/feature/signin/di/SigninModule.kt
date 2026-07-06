package com.arq.currencyconverter.feature.signin.di

import com.arq.currencyconverter.feature.signin.data.SigninLocalDataSource
import com.arq.currencyconverter.feature.signin.data.SigninLocalDataSourceImpl
import com.arq.currencyconverter.feature.signin.data.SigninRepositoryImpl
import com.arq.currencyconverter.feature.signin.domain.repository.SigninRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SigninModule {

    @Binds
    @Singleton
    abstract fun bindSigninLocalDataSource(impl: SigninLocalDataSourceImpl): SigninLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSigninRepository(impl: SigninRepositoryImpl): SigninRepository
}
