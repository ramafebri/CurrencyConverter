package com.arq.currencyconverter.feature.signup.data

interface SignupLocalDataSource {
    suspend fun createAccount(name: String, email: String, password: String)
    suspend fun emailExists(email: String): Boolean
}
