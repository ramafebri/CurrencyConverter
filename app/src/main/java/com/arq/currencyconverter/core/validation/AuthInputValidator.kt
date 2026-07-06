package com.arq.currencyconverter.core.validation

enum class AuthField {
    Name,
    Email,
    Password
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val fieldErrors: Map<AuthField, String>) : ValidationResult
}

object AuthInputValidator {
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 2

    private val namePattern = Regex("^[\\p{L} ]+$")
    private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateSignup(name: String, email: String, password: String): ValidationResult {
        val errors = mutableMapOf<AuthField, String>()
        validateName(name)?.let { errors[AuthField.Name] = it }
        validateEmail(email)?.let { errors[AuthField.Email] = it }
        validatePassword(password)?.let { errors[AuthField.Password] = it }
        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    fun validateSignin(email: String, password: String): ValidationResult {
        val errors = mutableMapOf<AuthField, String>()
        validateEmail(email)?.let { errors[AuthField.Email] = it }
        validatePassword(password)?.let { errors[AuthField.Password] = it }
        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    private fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isBlank() -> "Name is required"
            trimmed.length < MIN_NAME_LENGTH -> "Name must be at least $MIN_NAME_LENGTH characters"
            !namePattern.matches(trimmed) -> "Name can only contain letters and spaces"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isBlank() -> "Email is required"
            !emailPattern.matches(trimmed) -> "Enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required"

        password.length < MIN_PASSWORD_LENGTH ->
            "Password must be at least $MIN_PASSWORD_LENGTH characters"

        else -> null
    }
}
