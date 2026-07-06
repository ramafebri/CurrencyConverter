package com.arq.currencyconverter.feature.signin.data

interface SigninLocalDataSource {
    suspend fun signIn(email: String, password: String): SigninResult
}

sealed interface SigninResult {
    data class Success(val userId: Long) : SigninResult
    data object InvalidCredentials : SigninResult
}
