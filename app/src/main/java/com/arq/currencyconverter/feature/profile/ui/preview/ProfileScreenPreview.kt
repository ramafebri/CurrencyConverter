package com.arq.currencyconverter.feature.profile.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arq.currencyconverter.feature.profile.ui.ProfileScreenContent
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Preview(showBackground = true, name = "Profile Screen")
@Composable
private fun ProfileScreenPreview() {
    CurrencyConverterTheme {
        ProfileScreenContent(
            name = "Name Avatar",
            email = "currencyer@gmail.com",
            onLogout = {}
        )
    }
}
