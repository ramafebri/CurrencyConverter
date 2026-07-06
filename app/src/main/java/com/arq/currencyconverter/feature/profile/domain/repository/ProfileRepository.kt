package com.arq.currencyconverter.feature.profile.domain.repository

import com.arq.currencyconverter.core.common.UIResult

interface ProfileRepository {
    suspend fun logout(): UIResult<Unit>
}
