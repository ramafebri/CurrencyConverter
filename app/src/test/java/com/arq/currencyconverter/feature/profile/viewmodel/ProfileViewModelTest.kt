package com.arq.currencyconverter.feature.profile.viewmodel

import app.cash.turbine.test
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.feature.profile.domain.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val userSessionPreferences: UserSessionPreferences = mockk()
    private val profileRepository: ProfileRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val nameFlow = MutableStateFlow("")
    private val emailFlow = MutableStateFlow("")

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { userSessionPreferences.name } returns nameFlow
        every { userSessionPreferences.email } returns emailFlow

        viewModel = ProfileViewModel(userSessionPreferences, profileRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `name and email emit default values when preferences are empty`() = runTest {
        viewModel.name.test {
            assertEquals("Name Avatar", awaitItem())
        }
        viewModel.email.test {
            assertEquals("currencyer@gmail.com", awaitItem())
        }
    }

    @Test
    fun `name and email emit preference values when available`() = runTest {
        nameFlow.value = "John Doe"
        emailFlow.value = "john@example.com"

        viewModel.name.test {
            assertEquals("John Doe", awaitItem())
        }
        viewModel.email.test {
            assertEquals("john@example.com", awaitItem())
        }
    }

    @Test
    fun `logout calls repository logout`() = runTest {
        coEvery { profileRepository.logout() } returns UIResult.Success(Unit)

        viewModel.logout()

        coVerify { profileRepository.logout() }
    }
}
