package com.arq.currencyconverter.feature.signup.data

import com.arq.currencyconverter.core.data.local.db.UserAccountDao
import com.arq.currencyconverter.core.data.local.db.UserAccountEntity
import com.arq.currencyconverter.core.security.PasswordHasher
import com.arq.currencyconverter.di.DefaultDispatcher
import com.arq.currencyconverter.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SignupLocalDataSourceImpl @Inject constructor(
    private val userAccountDao: UserAccountDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : SignupLocalDataSource {

    override suspend fun createAccount(name: String, email: String, password: String) {
        val hashedPassword = withContext(defaultDispatcher) {
            PasswordHasher.hash(password)
        }
        withContext(ioDispatcher) {
            val entity = UserAccountEntity(
                name = name.trim(),
                email = email.trim().lowercase(),
                hashedPassword = hashedPassword,
                salt = PasswordHasher.SALT
            )
            userAccountDao.insert(entity)
        }
    }

    override suspend fun emailExists(email: String): Boolean = withContext(ioDispatcher) {
        userAccountDao.emailExists(email.trim().lowercase())
    }
}
