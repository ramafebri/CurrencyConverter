package com.arq.currencyconverter.di

import android.content.Context
import androidx.room.Room
import com.arq.currencyconverter.core.data.local.db.AppDatabase
import com.arq.currencyconverter.core.data.local.db.UserAccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "arq_finance.db"
        ).build()

    @Provides
    fun provideUserAccountDao(database: AppDatabase): UserAccountDao = database.userAccountDao()
}
