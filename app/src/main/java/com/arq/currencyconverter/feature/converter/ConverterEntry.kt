package com.arq.currencyconverter.feature.converter

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arq.currencyconverter.feature.converter.ui.ConverterScreen
import com.arq.currencyconverter.navigator.NavKeys

fun EntryProviderScope<NavKey>.converterEntry() {
    entry<NavKeys.Converter> {
        ConverterScreen()
    }
}
