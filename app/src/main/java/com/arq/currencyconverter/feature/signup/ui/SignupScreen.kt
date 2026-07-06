package com.arq.currencyconverter.feature.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.validation.AuthField
import com.arq.currencyconverter.feature.signup.viewmodel.SignupViewModel
import com.arq.currencyconverter.ui.components.UnderlineTextField

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val signupState by viewModel.signupState.collectAsStateWithLifecycle()
    val fieldErrors by viewModel.fieldErrors.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(signupState) {
        if (signupState is UIResult.Success) {
            onSignupSuccess()
        }
    }

    SignupScreenContent(
        signupState = signupState,
        fieldErrors = fieldErrors,
        isLoading = isLoading,
        onSignup = viewModel::signup,
        onNameChanged = viewModel::onNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        modifier = modifier
    )
}

@Composable
internal fun SignupScreenContent(
    signupState: UIResult<Unit>,
    fieldErrors: Map<AuthField, String>,
    isLoading: Boolean,
    onSignup: (name: String, email: String, password: String) -> Unit,
    onNameChanged: () -> Unit,
    onEmailChanged: () -> Unit,
    onPasswordChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }
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

        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        UnderlineTextField(
            value = name,
            onValueChange = {
                name = it
                onNameChanged()
            },
            placeholder = "Full Name",
            keyboardType = KeyboardType.Text,
            errorMessage = fieldErrors[AuthField.Name],
            modifier = Modifier.fillMaxWidth()
        )
        UnderlineTextField(
            value = email,
            onValueChange = {
                email = it
                onEmailChanged()
            },
            placeholder = "Email Address",
            keyboardType = KeyboardType.Email,
            errorMessage = fieldErrors[AuthField.Email],
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
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

        if (signupState is UIResult.Error) {
            Text(
                text = signupState.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSignup(name, email, password) },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
