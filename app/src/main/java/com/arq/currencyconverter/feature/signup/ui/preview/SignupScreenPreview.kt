package com.arq.currencyconverter.feature.signup.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.signup.ui.SignupScreenContent
import com.arq.currencyconverter.ui.theme.CurrencyConverterTheme

@Preview(showBackground = true, name = "Sign Up Screen")
@Composable
private fun SignupScreenPreview() {
    CurrencyConverterTheme {
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
