package com.arq.currencyconverter.di

import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSessionModule {

    @Binds
    @Singleton
    abstract fun bindUserSessionPreferences(
        impl: UserSessionPreferencesImpl
    ): UserSessionPreferences
}
