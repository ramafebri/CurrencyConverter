package com.arq.currencyconverter.core.data.local.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UserSessionPreferencesImplTest {

    private lateinit var preferences: UserSessionPreferencesImpl

    @Before
    fun setup() {
        preferences = UserSessionPreferencesImpl(InMemoryPreferencesDataStore())
    }

    @Test
    fun `sessionUserId emits null by default`() = runTest {
        preferences.sessionUserId.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `saveUser updates session data and emits correct values`() = runTest {
        preferences.saveUser(123L, "John Doe", "john@example.com")

        preferences.sessionUserId.test {
            assertEquals(123L, awaitItem())
        }
        preferences.name.test {
            assertEquals("John Doe", awaitItem())
        }
        preferences.email.test {
            assertEquals("john@example.com", awaitItem())
        }
    }

    @Test
    fun `clearUser removes session data`() = runTest {
        preferences.saveUser(123L, "John Doe", "john@example.com")
        preferences.clearUser()

        preferences.sessionUserId.test {
            assertNull(awaitItem())
        }
        preferences.name.test {
            assertEquals("", awaitItem())
        }
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val dataFlow = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = dataFlow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val updated = transform(dataFlow.value)
            dataFlow.value = updated
            return updated
        }
    }
}
