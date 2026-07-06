package com.arq.currencyconverter.feature.signup.di

import com.arq.currencyconverter.feature.signup.data.SignupLocalDataSource
import com.arq.currencyconverter.feature.signup.data.SignupLocalDataSourceImpl
import com.arq.currencyconverter.feature.signup.data.SignupRepositoryImpl
import com.arq.currencyconverter.feature.signup.domain.repository.SignupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SignupModule {

    @Binds
    @Singleton
    abstract fun bindSignupLocalDataSource(impl: SignupLocalDataSourceImpl): SignupLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSignupRepository(impl: SignupRepositoryImpl): SignupRepository
}
