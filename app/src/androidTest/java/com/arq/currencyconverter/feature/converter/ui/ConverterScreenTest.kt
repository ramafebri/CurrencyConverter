package com.arq.currencyconverter.feature.converter.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.converter.viewmodel.CurrencyBottomSheetViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

class ConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun converterScreen_initialState_displaysCurrenciesAndValues() {
        val exchangeRate = "1 USDc = 20.00 MXN\nSome secondary text"
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10.00",
                targetValue = "200.00",
                exchangeRateText = exchangeRate,
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        // Check currency codes
        composeTestRule.onNodeWithText("USDc").assertIsDisplayed()
        composeTestRule.onNodeWithText("MXN").assertIsDisplayed()

        // Check values
        composeTestRule.onNodeWithText("10.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("200.00").assertIsDisplayed()

        // Check exchange rate text
        composeTestRule.onNodeWithText("1 USDc = 20.00 MXN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Some secondary text").assertIsDisplayed()
    }

    @Test
    fun converterScreen_onSourceAmountChange_isCalled() {
        var capturedValue = ""
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "",
                targetValue = "",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = { capturedValue = it },
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("AmountTextField_USDc").performTextInput("15")
        assert(capturedValue == "15")
    }

    @Test
    fun converterScreen_onTargetAmountChange_isCalled() {
        var capturedValue = ""
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "",
                targetValue = "",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = { capturedValue = it },
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("AmountTextField_MXN").performTextInput("300")
        assert(capturedValue == "300")
    }

    @Test
    fun converterScreen_onSourceFieldFocused_isCalled() {
        var focused = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "0",
                targetValue = "0",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = { focused = true },
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("AmountTextField_USDc").performClick()
        assert(focused)
    }

    @Test
    fun converterScreen_onTargetFieldFocused_isCalled() {
        var focused = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "0",
                targetValue = "0",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = { focused = true },
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("AmountTextField_MXN").performClick()
        assert(focused)
    }

    @Test
    fun converterScreen_onDoneClick_isCalled() {
        var doneClicked = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = { doneClicked = true },
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("AmountTextField_USDc").performImeAction()
        assert(doneClicked)
    }

    @Test
    fun converterScreen_onSwapCurrencyClick_isCalled() {
        var swapClicked = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = { swapClicked = true },
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithContentDescription("Swap currencies").performClick()
        assert(swapClicked)
    }

    @Test
    fun converterScreen_onForeignCurrencyRowClick_isCalledFromTarget() {
        var rowClicked = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = { rowClicked = true },
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNode(
            hasTestTag("CurrencySelectorRow").and(hasAnyAncestor(hasTestTag("TargetCurrencyCard")))
        ).performClick()
        assert(rowClicked)
    }

    @Test
    fun converterScreen_onForeignCurrencyRowClick_isNotCalledFromSourceIfUSDc() {
        var rowClicked = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = { rowClicked = true },
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNode(
            hasTestTag("CurrencySelectorRow").and(hasAnyAncestor(hasTestTag("SourceCurrencyCard")))
        ).performClick()
        assert(!rowClicked)
    }

    @Test
    fun converterScreen_onForeignCurrencyRowClick_isCalledFromSourceWhenNotUsdC() {
        var rowClicked = false
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "MXN",
                targetCurrency = "USDc",
                sourceValue = "200",
                targetValue = "10",
                exchangeRateText = "",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = { rowClicked = true },
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNode(
            hasTestTag("CurrencySelectorRow").and(hasAnyAncestor(hasTestTag("SourceCurrencyCard")))
        ).performClick()
        assert(rowClicked)
    }

    @Test
    fun converterScreen_blankSecondaryRateText_isNotDisplayed() {
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "1 USDc = 20.00 MXN",
                isLoadingRate = false,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithText("1 USDc = 20.00 MXN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Current rate at").assertDoesNotExist()
    }

    @Test
    fun converterScreen_loadingRate_showsLoadingIndicator() {
        composeTestRule.setContent {
            ConverterScreenContent(
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                sourceValue = "10",
                targetValue = "200",
                exchangeRateText = "Rate",
                isLoadingRate = true,
                onSourceAmountChange = {},
                onTargetAmountChange = {},
                onSourceFieldFocused = {},
                onTargetFieldFocused = {},
                onFieldBlurred = {},
                onDoneClick = {},
                onSwapCurrencyClick = {},
                onForeignCurrencyRowClick = {},
                snackbarHostState = SnackbarHostState(),
            )
        }

        composeTestRule.onNodeWithTag("LoadingIndicator").assertIsDisplayed()
    }

    @Test
    fun currencyBottomSheet_searchFiltersCurrencyList() {
        val bottomSheetViewModel = CurrencyBottomSheetViewModel(Dispatchers.Main)
        composeTestRule.setContent {
            CurrencyBottomSheet(
                tickerCurrencyList = UIResult.Success(listOf("MXN", "BRL", "ARS")),
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                onDismissRequest = {},
                onCurrencySelected = {},
                viewModel = bottomSheetViewModel
            )
        }

        composeTestRule.onNodeWithTag("CurrencySearchField").performTextInput("br")

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("BRL").assertIsDisplayed()
        composeTestRule.onNodeWithText("MXN").assertDoesNotExist()
    }

    @Test
    fun currencyBottomSheet_selectCurrencyAfterSearch_callsCallback() {
        val bottomSheetViewModel = CurrencyBottomSheetViewModel(Dispatchers.Main)
        var selectedCurrency = ""
        composeTestRule.setContent {
            CurrencyBottomSheet(
                tickerCurrencyList = UIResult.Success(listOf("MXN", "BRL", "ARS")),
                sourceCurrency = "USDc",
                targetCurrency = "MXN",
                onDismissRequest = {},
                onCurrencySelected = { selectedCurrency = it },
                viewModel = bottomSheetViewModel
            )
        }

        composeTestRule.onNodeWithTag("CurrencySearchField").performTextInput("ar")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("ARS").performClick()

        assert(selectedCurrency == "ARS")
    }
}
