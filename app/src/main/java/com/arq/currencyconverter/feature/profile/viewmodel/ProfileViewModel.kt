package com.arq.currencyconverter.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userSessionPreferences: UserSessionPreferences,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val name: StateFlow<String> = userSessionPreferences.name
        .map { it.ifBlank { DEFAULT_NAME } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DEFAULT_NAME)

    val email: StateFlow<String> = userSessionPreferences.email
        .map { it.ifBlank { DEFAULT_EMAIL } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DEFAULT_EMAIL)

    fun logout() {
        viewModelScope.launch {
            profileRepository.logout()
        }
    }

    private companion object {
        const val DEFAULT_NAME = "Name Avatar"
        const val DEFAULT_EMAIL = "currencyer@gmail.com"
    }
}
