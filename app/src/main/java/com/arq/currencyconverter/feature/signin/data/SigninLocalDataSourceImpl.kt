package com.arq.currencyconverter.feature.signin.data

import com.arq.currencyconverter.core.data.local.db.UserAccountDao
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.core.security.PasswordHasher
import com.arq.currencyconverter.di.DefaultDispatcher
import com.arq.currencyconverter.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SigninLocalDataSourceImpl @Inject constructor(
    private val userAccountDao: UserAccountDao,
    private val userSessionPreferences: UserSessionPreferences,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : SigninLocalDataSource {

    override suspend fun signIn(email: String, password: String): SigninResult {
        val user = withContext(ioDispatcher) {
            userAccountDao.getByEmail(email.trim().lowercase())
        } ?: return SigninResult.InvalidCredentials

        val hashedPassword = withContext(defaultDispatcher) {
            PasswordHasher.hash(password)
        }
        if (user.hashedPassword != hashedPassword) {
            return SigninResult.InvalidCredentials
        }

        withContext(ioDispatcher) {
            userSessionPreferences.saveUser(user.id, user.name, user.email)
        }
        return SigninResult.Success(user.id)
    }
}
