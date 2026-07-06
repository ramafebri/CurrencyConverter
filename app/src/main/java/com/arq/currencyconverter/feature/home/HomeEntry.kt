package com.arq.currencyconverter.feature.home

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arq.currencyconverter.feature.home.ui.HomeScreen
import com.arq.currencyconverter.navigator.NavKeys
import com.arq.currencyconverter.navigator.Navigator

fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<NavKeys.Home> {
        HomeScreen(
            onNavigateToConverter = { navigator.toConverter() },
            onNavigateToProfile = { navigator.toProfile() }
        )
    }
}
