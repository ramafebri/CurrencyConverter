package com.arq.currencyconverter.navigator.sessionobserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SessionViewModel @Inject constructor(userSessionPreferences: UserSessionPreferences) :
    ViewModel() {

    val sessionCheckState: StateFlow<SessionCheckState> =
        userSessionPreferences.sessionUserId
            .map { userId -> SessionCheckState.Resolved(userId) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                SessionCheckState.Loading
            )
}
