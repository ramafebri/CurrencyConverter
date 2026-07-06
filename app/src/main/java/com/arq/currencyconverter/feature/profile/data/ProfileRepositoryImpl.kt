package com.arq.currencyconverter.feature.profile.data

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val localDataSource: ProfileLocalDataSource
) : ProfileRepository {

    override suspend fun logout(): UIResult<Unit> = try {
        localDataSource.logout()
        UIResult.Success(Unit)
    } catch (exception: Exception) {
        UIResult.Error("Unable to log out", exception)
    }
}
