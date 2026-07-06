package com.arq.currencyconverter.feature.profile.data

import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileLocalDataSourceImplTest {

    private val userSessionPreferences: UserSessionPreferences = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: ProfileLocalDataSourceImpl

    @Before
    fun setup() {
        dataSource = ProfileLocalDataSourceImpl(userSessionPreferences, testDispatcher)
    }

    @Test
    fun `logout calls clearUser on preferences`() = runTest {
        dataSource.logout()

        coVerify { userSessionPreferences.clearUser() }
    }
}
