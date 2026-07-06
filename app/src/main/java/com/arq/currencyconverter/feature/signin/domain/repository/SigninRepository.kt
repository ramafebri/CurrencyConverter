package com.arq.currencyconverter.feature.signin.domain.repository

import com.arq.currencyconverter.core.common.UIResult

interface SigninRepository {
    suspend fun signIn(email: String, password: String): UIResult<Unit>
}
