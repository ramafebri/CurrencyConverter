package com.arq.currencyconverter.feature.signup

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arq.currencyconverter.feature.signup.ui.SignupScreen
import com.arq.currencyconverter.navigator.NavKeys
import com.arq.currencyconverter.navigator.Navigator

fun EntryProviderScope<NavKey>.signupEntry(navigator: Navigator) {
    entry<NavKeys.Signup> {
        SignupScreen(
            onSignupSuccess = { navigator.goBack() }
        )
    }
}
