package com.arq.currencyconverter.feature.profile.di

import com.arq.currencyconverter.feature.profile.data.ProfileLocalDataSource
import com.arq.currencyconverter.feature.profile.data.ProfileLocalDataSourceImpl
import com.arq.currencyconverter.feature.profile.data.ProfileRepositoryImpl
import com.arq.currencyconverter.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(
        impl: ProfileLocalDataSourceImpl
    ): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
