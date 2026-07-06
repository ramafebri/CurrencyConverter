package com.arq.currencyconverter.feature.signin

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.arq.currencyconverter.feature.signin.ui.SigninScreen
import com.arq.currencyconverter.navigator.NavKeys
import com.arq.currencyconverter.navigator.Navigator

fun EntryProviderScope<NavKey>.signinEntry(navigator: Navigator) {
    entry<NavKeys.Signin> {
        SigninScreen(
            onNavigateToSignup = { navigator.toSignup() },
            onSignInSuccess = { navigator.toHome() }
        )
    }
}
