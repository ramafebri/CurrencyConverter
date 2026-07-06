package com.arq.currencyconverter.navigator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.splashEntry() {
    entry<NavKeys.Splash> {
        Box(modifier = Modifier.fillMaxSize())
    }
}
