package com.arq.currencyconverter.feature.signup.data

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.signup.domain.repository.SignupRepository
import javax.inject.Inject

class SignupRepositoryImpl @Inject constructor(private val localDataSource: SignupLocalDataSource) :
    SignupRepository {

    override suspend fun signup(name: String, email: String, password: String): UIResult<Unit> =
        try {
            if (localDataSource.emailExists(email)) {
                UIResult.Error("Email already registered")
            } else {
                localDataSource.createAccount(name, email, password)
                UIResult.Success(Unit)
            }
        } catch (exception: Exception) {
            UIResult.Error("Unable to create account", exception)
        }
}
