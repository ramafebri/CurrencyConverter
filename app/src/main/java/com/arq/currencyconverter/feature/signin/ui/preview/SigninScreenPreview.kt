package com.arq.currencyconverter.feature.signin.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.signin.ui.SigninScreenContent
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Preview(showBackground = true, name = "Sign In Screen")
@Composable
private fun SigninScreenPreview() {
    CurrencyConverterTheme {
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
