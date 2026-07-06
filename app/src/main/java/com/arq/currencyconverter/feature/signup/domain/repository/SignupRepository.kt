package com.arq.currencyconverter.feature.signup.domain.repository

import com.arq.currencyconverter.core.common.UIResult

interface SignupRepository {
    suspend fun signup(name: String, email: String, password: String): UIResult<Unit>
}
