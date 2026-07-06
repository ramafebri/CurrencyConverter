package com.arq.currencyconverter.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserAccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
}
