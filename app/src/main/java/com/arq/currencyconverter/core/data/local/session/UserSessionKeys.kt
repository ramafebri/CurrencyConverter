package com.arq.currencyconverter.core.data.local.session

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserSessionKeys {
    val SESSION_USER_ID = longPreferencesKey("session_user_id")
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
}
