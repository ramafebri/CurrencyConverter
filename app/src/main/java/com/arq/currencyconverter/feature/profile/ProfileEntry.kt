package com.arq.currencyconverter.feature.profile

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arq.currencyconverter.feature.profile.ui.ProfileScreen
import com.arq.currencyconverter.navigator.NavKeys

fun EntryProviderScope<NavKey>.profileEntry() {
    entry<NavKeys.Profile> {
        ProfileScreen()
    }
}
