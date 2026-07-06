package com.arq.currencyconverter

import android.app.Application
import com.arq.currencyconverter.core.common.StrictModeInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CurrencyConverterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictModeInitializer.initialize()
        }
    }
}
