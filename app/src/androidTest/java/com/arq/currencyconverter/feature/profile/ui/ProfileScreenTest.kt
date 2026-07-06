package com.arq.currencyconverter.feature.profile.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreenContent_displaysNameEmailAndSettings() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                ProfileScreenContent(
                    name = "John Doe",
                    email = "john@example.com",
                    onLogout = {}
                )
            }
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john@example.com").assertIsDisplayed()

        composeTestRule.onNodeWithText("Payment Methods").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notification Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Security Preferences").assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency Alerts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transaction History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Help Center").assertIsDisplayed()
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("Log Out").assertIsDisplayed()
    }

    @Test
    fun profileScreenContent_onLogoutClick_invokesCallback() {
        var onLogoutCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                ProfileScreenContent(
                    name = "John Doe",
                    email = "john@example.com",
                    onLogout = { onLogoutCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Log Out").performClick()
        assertTrue(onLogoutCalled)
    }
}
