package com.arq.currencyconverter.feature.signin.viewmodel

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.core.validation.AuthInputValidator
import com.arq.currencyconverter.core.validation.ValidationResult
import com.arq.currencyconverter.feature.signin.domain.repository.SigninRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SigninViewModelTest {

    private val repository: SigninRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SigninViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(AuthInputValidator)
        viewModel = SigninViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(AuthInputValidator)
    }

    @Test
    fun `signIn sets fieldErrors and Empty state when validation fails`() = runTest {
        val errors = mapOf(AuthField.Email to "Invalid email")
        every {
            AuthInputValidator.validateSignin(
                any(),
                any()
            )
        } returns ValidationResult.Invalid(errors)

        viewModel.signIn("invalid", "pass")

        assertEquals(errors, viewModel.fieldErrors.value)
        assertEquals(UIResult.Empty, viewModel.signInState.value)
    }

    @Test
    fun `signIn calls repository and updates state when validation succeeds`() = runTest {
        every { AuthInputValidator.validateSignin(any(), any()) } returns ValidationResult.Valid
        coEvery { repository.signIn(any(), any()) } returns UIResult.Success(Unit)

        viewModel.signIn("test@example.com", "password")

        assertEquals(emptyMap<AuthField, String>(), viewModel.fieldErrors.value)
        assertEquals(UIResult.Success(Unit), viewModel.signInState.value)
    }

    @Test
    fun `onEmailChanged clears email error`() = runTest {
        val errors = mapOf(AuthField.Email to "Invalid email", AuthField.Password to "Error")
        every {
            AuthInputValidator.validateSignin(
                any(),
                any()
            )
        } returns ValidationResult.Invalid(errors)

        viewModel.signIn("invalid", "pass")
        assertEquals(2, viewModel.fieldErrors.value.size)

        viewModel.onEmailChanged()

        assertEquals(1, viewModel.fieldErrors.value.size)
        assert(!viewModel.fieldErrors.value.containsKey(AuthField.Email))
    }
}
