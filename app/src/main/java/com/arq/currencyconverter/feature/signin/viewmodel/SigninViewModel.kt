package com.arq.currencyconverter.feature.signin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.core.validation.AuthInputValidator
import com.arq.currencyconverter.core.validation.ValidationResult
import com.arq.currencyconverter.feature.signin.domain.repository.SigninRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SigninViewModel @Inject constructor(private val signinRepository: SigninRepository) :
    ViewModel() {

    private val _signInState = MutableStateFlow<UIResult<Unit>>(UIResult.Empty)
    val signInState: StateFlow<UIResult<Unit>> = _signInState.asStateFlow()

    private val _fieldErrors = MutableStateFlow<Map<AuthField, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<AuthField, String>> = _fieldErrors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onEmailChanged() {
        clearFieldError(AuthField.Email)
    }

    fun onPasswordChanged() {
        clearFieldError(AuthField.Password)
    }

    fun signIn(email: String, password: String) {
        when (val validation = AuthInputValidator.validateSignin(email, password)) {
            is ValidationResult.Invalid -> {
                _fieldErrors.value = validation.fieldErrors
                _signInState.value = UIResult.Empty
                return
            }

            ValidationResult.Valid -> {
                _fieldErrors.value = emptyMap()
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _signInState.value = signinRepository.signIn(email, password)
            _isLoading.value = false
        }
    }

    private fun clearFieldError(field: AuthField) {
        if (_fieldErrors.value.containsKey(field)) {
            _fieldErrors.value -= field
        }
    }
}
