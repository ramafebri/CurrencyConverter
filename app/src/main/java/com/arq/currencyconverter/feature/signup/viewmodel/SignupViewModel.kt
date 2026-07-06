package com.arq.currencyconverter.feature.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.core.validation.AuthInputValidator
import com.arq.currencyconverter.core.validation.ValidationResult
import com.arq.currencyconverter.feature.signup.domain.repository.SignupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(private val signupRepository: SignupRepository) :
    ViewModel() {

    private val _signupState = MutableStateFlow<UIResult<Unit>>(UIResult.Empty)
    val signupState: StateFlow<UIResult<Unit>> = _signupState.asStateFlow()

    private val _fieldErrors = MutableStateFlow<Map<AuthField, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<AuthField, String>> = _fieldErrors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onNameChanged() {
        clearFieldError(AuthField.Name)
    }

    fun onEmailChanged() {
        clearFieldError(AuthField.Email)
    }

    fun onPasswordChanged() {
        clearFieldError(AuthField.Password)
    }

    fun signup(name: String, email: String, password: String) {
        when (val validation = AuthInputValidator.validateSignup(name, email, password)) {
            is ValidationResult.Invalid -> {
                _fieldErrors.value = validation.fieldErrors
                _signupState.value = UIResult.Empty
                return
            }

            ValidationResult.Valid -> {
                _fieldErrors.value = emptyMap()
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _signupState.value = signupRepository.signup(name, email, password)
            _isLoading.value = false
        }
    }

    private fun clearFieldError(field: AuthField) {
        if (_fieldErrors.value.containsKey(field)) {
            _fieldErrors.value -= field
        }
    }
}
