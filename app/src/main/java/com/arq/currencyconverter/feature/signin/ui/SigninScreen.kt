package com.arq.currencyconverter.feature.signin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.feature.signin.viewmodel.SigninViewModel
import com.arq.currencyconverter.ui.components.UnderlineTextField

@Composable
fun SigninScreen(
    onNavigateToSignup: () -> Unit,
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SigninViewModel = hiltViewModel()
) {
    val signInState by viewModel.signInState.collectAsStateWithLifecycle()
    val fieldErrors by viewModel.fieldErrors.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(signInState) {
        if (signInState is UIResult.Success) {
            onSignInSuccess()
        }
    }

    SigninScreenContent(
        signInState = signInState,
        fieldErrors = fieldErrors,
        isLoading = isLoading,
        onNavigateToSignup = onNavigateToSignup,
        onSignIn = viewModel::signIn,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        modifier = modifier
    )
}

@Composable
internal fun SigninScreenContent(
    signInState: UIResult<Unit>,
    fieldErrors: Map<AuthField, String>,
    isLoading: Boolean,
    onNavigateToSignup: () -> Unit,
    onSignIn: (email: String, password: String) -> Unit,
    onEmailChanged: () -> Unit,
    onPasswordChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        SigninLogo()
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Welcome Back.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        UnderlineTextField(
            value = email,
            onValueChange = {
                email = it
                onEmailChanged()
            },
            placeholder = "Email Address",
            keyboardType = KeyboardType.Email,
            errorMessage = fieldErrors[AuthField.Email],
            modifier = Modifier.fillMaxWidth()
        )
        UnderlineTextField(
            value = password,
            onValueChange = {
                password = it
                onPasswordChanged()
            },
            placeholder = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            errorMessage = fieldErrors[AuthField.Password],
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )

        if (signInState is UIResult.Error) {
            Text(
                text = signInState.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Button(
            onClick = { onSignIn(email, password) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        SignUpFooter(
            onNavigateToSignup = onNavigateToSignup,
            modifier = Modifier.padding(bottom = 40.dp)
        )
    }
}

@Composable
private fun SigninLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 52.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.tertiary)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = "Currency",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Converter",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun SignUpFooter(onNavigateToSignup: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Don't have an account? ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable(onClick = onNavigateToSignup)
        )
    }
}
