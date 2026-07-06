package com.arq.currencyconverter.feature.signup.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.feature.signup.domain.repository.SignupRepository
import com.arq.currencyconverter.feature.signup.viewmodel.SignupViewModel
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signupScreenContent_initialState_displaysLabelsAndButton() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreenContent(
                    signupState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onSignup = { _, _, _ -> },
                    onNameChanged = {},
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create an Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email Address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
    }

    @Test
    fun signupScreenContent_inputAndSubmit_invokesCallbacksWithPayload() {
        var capturedName = ""
        var capturedEmail = ""
        var capturedPassword = ""
        var nameChangedCalled = false
        var emailChangedCalled = false
        var passwordChangedCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreenContent(
                    signupState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onSignup = { name, email, password ->
                        capturedName = name
                        capturedEmail = email
                        capturedPassword = password
                    },
                    onNameChanged = { nameChangedCalled = true },
                    onEmailChanged = { emailChangedCalled = true },
                    onPasswordChanged = { passwordChangedCalled = true }
                )
            }
        }

        composeTestRule.textFieldAt(0).performTextInput("John Doe")
        composeTestRule.textFieldAt(1).performTextInput("john@example.com")
        composeTestRule.textFieldAt(2).performTextInput("secret123")
        composeTestRule.onNodeWithText("Create Account").performClick()

        assertTrue(nameChangedCalled)
        assertTrue(emailChangedCalled)
        assertTrue(passwordChangedCalled)
        assertEquals("John Doe", capturedName)
        assertEquals("john@example.com", capturedEmail)
        assertEquals("secret123", capturedPassword)
    }

    @Test
    fun signupScreenContent_loadingState_disablesCreateAccountButton() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreenContent(
                    signupState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = true,
                    onSignup = { _, _, _ -> },
                    onNameChanged = {},
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Create Account").assertIsNotEnabled()
    }

    @Test
    fun signupScreenContent_fieldErrors_displaysAllValidationMessages() {
        val fieldErrors = mapOf(
            AuthField.Name to "Name is required",
            AuthField.Email to "Enter a valid email address",
            AuthField.Password to "Password must be at least 6 characters"
        )

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreenContent(
                    signupState = UIResult.Empty,
                    fieldErrors = fieldErrors,
                    isLoading = false,
                    onSignup = { _, _, _ -> },
                    onNameChanged = {},
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Name is required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter a valid email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun signupScreenContent_errorState_displaysGlobalErrorMessage() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreenContent(
                    signupState = UIResult.Error("Email already registered"),
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onSignup = { _, _, _ -> },
                    onNameChanged = {},
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Email already registered").assertIsDisplayed()
    }

    @Test
    fun signupScreen_successfulSignup_invokesOnSignupSuccess() {
        var onSignupSuccessCalled = false
        val viewModel = SignupViewModel(
            signupRepository = object : SignupRepository {
                override suspend fun signup(name: String, email: String, password: String): UIResult<Unit> {
                    return UIResult.Success(Unit)
                }
            }
        )

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SignupScreen(
                    onSignupSuccess = { onSignupSuccessCalled = true },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.textFieldAt(0).performTextInput("John Doe")
        composeTestRule.textFieldAt(1).performTextInput("john@example.com")
        composeTestRule.textFieldAt(2).performTextInput("secret123")
        composeTestRule.onNodeWithText("Create Account").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) { onSignupSuccessCalled }
        assertTrue(onSignupSuccessCalled)
    }

    private fun ComposeContentTestRule.textFieldAt(index: Int) =
        onAllNodes(hasSetTextAction(), useUnmergedTree = true)[index]
}
