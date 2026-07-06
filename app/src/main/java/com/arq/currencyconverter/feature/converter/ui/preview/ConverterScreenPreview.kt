package com.arq.currencyconverter.feature.converter.ui.preview

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.arq.currencyconverter.feature.converter.ui.ConverterScreenContent
import com.arq.currencyconverter.feature.converter.viewmodel.ConverterViewModel
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Preview(showBackground = true, name = "Converter Screen")
@Composable
private fun ConverterScreenPreview() {
    CurrencyConverterTheme {
        ConverterScreenContent(
            sourceCurrency = ConverterViewModel.USDC_CURRENCY,
            targetCurrency = "MXN",
            sourceValue = "100.00",
            targetValue = "1,725.50",
            exchangeRateText = "1 USDc = 17.2550 MXN\nLast updated: Jun 18, 2026",
            isLoadingRate = false,
            onSourceAmountChange = {},
            onTargetAmountChange = {},
            onSourceFieldFocused = {},
            onTargetFieldFocused = {},
            onFieldBlurred = {},
            onDoneClick = {},
            onSwapCurrencyClick = {},
            onForeignCurrencyRowClick = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
