package com.arq.currencyconverter.feature.home.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arq.currencyconverter.feature.home.ui.HomeScreen
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Preview(showBackground = true, name = "Home Screen")
@Composable
private fun HomeScreenPreview() {
    CurrencyConverterTheme {
        HomeScreen(
            onNavigateToConverter = {},
            onNavigateToProfile = {}
        )
    }
}
