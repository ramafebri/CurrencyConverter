package com.arq.currencyconverter.core.data.local.session

import kotlinx.coroutines.flow.Flow

interface UserSessionPreferences {
    val sessionUserId: Flow<Long?>
    val name: Flow<String>
    val email: Flow<String>

    suspend fun saveUser(id: Long?, name: String, email: String)

    suspend fun clearUser()
}
