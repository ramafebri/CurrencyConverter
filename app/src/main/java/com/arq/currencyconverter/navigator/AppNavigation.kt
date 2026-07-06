package com.arq.currencyconverter.navigator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.arq.currencyconverter.feature.converter.converterEntry
import com.arq.currencyconverter.feature.home.homeEntry
import com.arq.currencyconverter.feature.profile.profileEntry
import com.arq.currencyconverter.feature.signin.signinEntry
import com.arq.currencyconverter.feature.signup.signupEntry
import com.arq.currencyconverter.navigator.sessionobserver.SessionObserver
import com.arq.currencyconverter.navigator.sessionobserver.SessionViewModel
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Composable
fun AppNavigation(
    navigator: Navigator,
    onSessionReady: () -> Unit = {},
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val sessionCheckState by sessionViewModel.sessionCheckState.collectAsStateWithLifecycle()

    CurrencyConverterTheme {
        SessionObserver(
            navigator = navigator,
            sessionCheckState = sessionCheckState,
            onSessionReady = onSessionReady,
        )

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    splashEntry()
                    homeEntry(navigator)
                    converterEntry()
                    signupEntry(navigator)
                    signinEntry(navigator)
                    profileEntry()
                }
            )
        }
    }
}
