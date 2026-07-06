package com.arq.currencyconverter.feature.signin.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.feature.signin.domain.repository.SigninRepository
import com.arq.currencyconverter.feature.signin.viewmodel.SigninViewModel
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SigninScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signinScreenContent_initialState_displaysCoreUiElements() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onNavigateToSignup = {},
                    onSignIn = { _, _ -> },
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Converter").assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome Back.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email Address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun signinScreenContent_inputAndSubmit_invokesCallbacksWithPayload() {
        var capturedEmail = ""
        var capturedPassword = ""
        var emailChangedCalled = false
        var passwordChangedCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onNavigateToSignup = {},
                    onSignIn = { email, password ->
                        capturedEmail = email
                        capturedPassword = password
                    },
                    onEmailChanged = { emailChangedCalled = true },
                    onPasswordChanged = { passwordChangedCalled = true }
                )
            }
        }

        composeTestRule.textFieldAt(0).performTextInput("john@example.com")
        composeTestRule.textFieldAt(1).performTextInput("secret123")
        composeTestRule.signInButton().performClick()

        assertTrue(emailChangedCalled)
        assertTrue(passwordChangedCalled)
        assertEquals("john@example.com", capturedEmail)
        assertEquals("secret123", capturedPassword)
    }

    @Test
    fun signinScreenContent_loadingState_disablesSignInButton() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = true,
                    onNavigateToSignup = {},
                    onSignIn = { _, _ -> },
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.signInButton().assertIsNotEnabled()
    }

    @Test
    fun signinScreenContent_fieldErrors_displaysValidationMessages() {
        val fieldErrors = mapOf(
            AuthField.Email to "Enter a valid email address",
            AuthField.Password to "Password must be at least 6 characters"
        )

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Empty,
                    fieldErrors = fieldErrors,
                    isLoading = false,
                    onNavigateToSignup = {},
                    onSignIn = { _, _ -> },
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Enter a valid email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun signinScreenContent_errorState_displaysGlobalErrorMessage() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Error("Invalid email or password"),
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onNavigateToSignup = {},
                    onSignIn = { _, _ -> },
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Invalid email or password").assertIsDisplayed()
    }

    @Test
    fun signinScreenContent_onSignupLinkClick_invokesNavigationCallback() {
        var navigateToSignupCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreenContent(
                    signInState = UIResult.Empty,
                    fieldErrors = emptyMap(),
                    isLoading = false,
                    onNavigateToSignup = { navigateToSignupCalled = true },
                    onSignIn = { _, _ -> },
                    onEmailChanged = {},
                    onPasswordChanged = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Sign Up").performClick()
        assertTrue(navigateToSignupCalled)
    }

    @Test
    fun signinScreen_successfulSignin_invokesOnSignInSuccess() {
        var onSignInSuccessCalled = false
        val viewModel = SigninViewModel(
            signinRepository = object : SigninRepository {
                override suspend fun signIn(email: String, password: String): UIResult<Unit> {
                    return UIResult.Success(Unit)
                }
            }
        )

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                SigninScreen(
                    onNavigateToSignup = {},
                    onSignInSuccess = { onSignInSuccessCalled = true },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.textFieldAt(0).performTextInput("john@example.com")
        composeTestRule.textFieldAt(1).performTextInput("secret123")
        composeTestRule.signInButton().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) { onSignInSuccessCalled }
        assertTrue(onSignInSuccessCalled)
    }

    private fun ComposeContentTestRule.textFieldAt(index: Int) =
        onAllNodes(hasSetTextAction(), useUnmergedTree = true)[index]

    private fun ComposeContentTestRule.signInButton() =
        onNode(hasClickAction() and hasText("Sign In"))
}
