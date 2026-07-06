package com.arq.currencyconverter.navigator.sessionobserver

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.arq.currencyconverter.navigator.NavKeys
import com.arq.currencyconverter.navigator.Navigator
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun SessionObserver(
    navigator: Navigator,
    sessionCheckState: SessionCheckState,
    onSessionReady: () -> Unit = {},
) {
    var currentDestination by remember(navigator) {
        mutableStateOf(navigator.backStack.lastOrNull())
    }

    LaunchedEffect(navigator) {
        snapshotFlow { navigator.backStack.lastOrNull() }
            .distinctUntilChanged()
            .collect { destination ->
                currentDestination = destination
            }
    }

    LaunchedEffect(sessionCheckState) {
        if (sessionCheckState is SessionCheckState.Resolved) {
            onSessionReady()
        }
    }

    LaunchedEffect(sessionCheckState, currentDestination) {
        val resolvedState = sessionCheckState as? SessionCheckState.Resolved ?: return@LaunchedEffect

        val isAuthScreen =
            currentDestination is NavKeys.Signin || currentDestination is NavKeys.Signup
        when {
            currentDestination is NavKeys.Splash -> {
                if (resolvedState.userId != null) {
                    navigator.toHome()
                } else {
                    navigator.toSignin()
                }
            }
            resolvedState.userId == null && !isAuthScreen -> navigator.toSignin()
            resolvedState.userId != null && currentDestination is NavKeys.Signin -> navigator.toHome()
        }
    }
}
