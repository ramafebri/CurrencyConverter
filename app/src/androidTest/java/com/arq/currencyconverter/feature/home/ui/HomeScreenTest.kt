package com.arq.currencyconverter.feature.home.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_initialState_displaysPortfolioAndBalances() {
        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                HomeScreen(onNavigateToConverter = {}, onNavigateToProfile = {})
            }
        }

        composeTestRule.onNodeWithText("My Portfolio").assertIsDisplayed()
        composeTestRule.onNodeWithText("Main balances").assertIsDisplayed()

        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("$12,500.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("€10,200.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("GER").assertIsDisplayed()
        composeTestRule.onNodeWithText("€12,500.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("USK").assertIsDisplayed()
        composeTestRule.onNodeWithText("$0.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start New Conversion").assertIsDisplayed()
    }

    @Test
    fun homeScreen_onStartNewConversionClick_invokesCallback() {
        var navigateToConverterCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                HomeScreen(
                    onNavigateToConverter = { navigateToConverterCalled = true },
                    onNavigateToProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Start New Conversion").performClick()
        assertTrue(navigateToConverterCalled)
    }

    @Test
    fun homeScreen_onProfileAvatarClick_invokesCallback() {
        var navigateToProfileCalled = false

        composeTestRule.setContent {
            CurrencyConverterTheme(dynamicColor = false) {
                HomeScreen(
                    onNavigateToConverter = {},
                    onNavigateToProfile = { navigateToProfileCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Profile").performClick()
        assertTrue(navigateToProfileCalled)
    }
}
