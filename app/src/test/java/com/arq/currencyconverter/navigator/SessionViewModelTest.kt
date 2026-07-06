package com.arq.currencyconverter.navigator

import app.cash.turbine.test
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
import com.arq.currencyconverter.navigator.sessionobserver.SessionCheckState
import com.arq.currencyconverter.navigator.sessionobserver.SessionViewModel
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
class SessionViewModelTest {

    private val userSessionPreferences: UserSessionPreferences = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sessionUserIdFlow = MutableStateFlow<Long?>(null)

    private lateinit var viewModel: SessionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { userSessionPreferences.sessionUserId } returns sessionUserIdFlow
        viewModel = SessionViewModel(userSessionPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sessionCheckState emits resolved values from preferences`() = runTest {
        viewModel.sessionCheckState.test {
            when (val initialState = awaitItem()) {
                SessionCheckState.Loading ->
                    assertEquals(SessionCheckState.Resolved(null), awaitItem())
                is SessionCheckState.Resolved ->
                    assertEquals(null, initialState.userId)
            }

            sessionUserIdFlow.value = 123L
            assertEquals(SessionCheckState.Resolved(123L), awaitItem())
        }
    }
}
