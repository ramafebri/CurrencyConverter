package com.arq.currencyconverter.feature.signup.viewmodel

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.core.validation.AuthInputValidator
import com.arq.currencyconverter.core.validation.ValidationResult
import com.arq.currencyconverter.feature.signup.domain.repository.SignupRepository
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
class SignupViewModelTest {

    private val repository: SignupRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SignupViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(AuthInputValidator)
        viewModel = SignupViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(AuthInputValidator)
    }

    @Test
    fun `signup sets fieldErrors and Empty state when validation fails`() = runTest {
        val errors = mapOf(AuthField.Name to "Invalid name")
        every {
            AuthInputValidator.validateSignup(
                any(),
                any(),
                any()
            )
        } returns ValidationResult.Invalid(errors)

        viewModel.signup("n", "e", "p")

        assertEquals(errors, viewModel.fieldErrors.value)
        assertEquals(UIResult.Empty, viewModel.signupState.value)
    }

    @Test
    fun `signup calls repository and updates state when validation succeeds`() = runTest {
        every {
            AuthInputValidator.validateSignup(
                any(),
                any(),
                any()
            )
        } returns ValidationResult.Valid
        coEvery { repository.signup(any(), any(), any()) } returns UIResult.Success(Unit)

        viewModel.signup("Name", "test@example.com", "password")

        assertEquals(emptyMap<AuthField, String>(), viewModel.fieldErrors.value)
        assertEquals(UIResult.Success(Unit), viewModel.signupState.value)
    }

    @Test
    fun `onNameChanged clears name error`() = runTest {
        val errors = mapOf(AuthField.Name to "Error", AuthField.Email to "Error")
        every {
            AuthInputValidator.validateSignup(
                any(),
                any(),
                any()
            )
        } returns ValidationResult.Invalid(errors)

        viewModel.signup("n", "e", "p")
        assertEquals(2, viewModel.fieldErrors.value.size)

        viewModel.onNameChanged()

        assertEquals(1, viewModel.fieldErrors.value.size)
        assert(!viewModel.fieldErrors.value.containsKey(AuthField.Name))
    }
}
