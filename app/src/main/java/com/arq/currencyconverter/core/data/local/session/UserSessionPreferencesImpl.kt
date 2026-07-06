package com.arq.currencyconverter.core.data.local.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSessionPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserSessionPreferences {

    override val sessionUserId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[UserSessionKeys.SESSION_USER_ID]
    }

    override val name: Flow<String> = dataStore.data.map { preferences ->
        preferences[UserSessionKeys.NAME] ?: ""
    }

    override val email: Flow<String> = dataStore.data.map { preferences ->
        preferences[UserSessionKeys.EMAIL] ?: ""
    }

    override suspend fun saveUser(id: Long?, name: String, email: String) {
        dataStore.edit { preferences ->
            if (id == null) {
                preferences.remove(UserSessionKeys.SESSION_USER_ID)
            } else {
                preferences[UserSessionKeys.SESSION_USER_ID] = id
            }
            preferences[UserSessionKeys.NAME] = name
            preferences[UserSessionKeys.EMAIL] = email
        }
    }

    override suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(UserSessionKeys.SESSION_USER_ID)
            preferences.remove(UserSessionKeys.NAME)
            preferences.remove(UserSessionKeys.EMAIL)
        }
    }
}
