package com.arq.currencyconverter.feature.profile.data

import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.di.IoDispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ProfileLocalDataSourceImpl @Inject constructor(
    private val userSessionPreferences: UserSessionPreferences,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileLocalDataSource {

    override suspend fun logout() = withContext(ioDispatcher) {
        userSessionPreferences.clearUser()
    }
}
