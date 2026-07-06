package com.arq.currencyconverter.feature.signin.data

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.signin.domain.repository.SigninRepository
import javax.inject.Inject

class SigninRepositoryImpl @Inject constructor(private val localDataSource: SigninLocalDataSource) :
    SigninRepository {

    override suspend fun signIn(email: String, password: String): UIResult<Unit> = try {
        when (localDataSource.signIn(email, password)) {
            is SigninResult.Success -> UIResult.Success(Unit)
            SigninResult.InvalidCredentials -> UIResult.Error("Invalid email or password")
        }
    } catch (exception: Exception) {
        UIResult.Error("Unable to sign in", exception)
    }
}
