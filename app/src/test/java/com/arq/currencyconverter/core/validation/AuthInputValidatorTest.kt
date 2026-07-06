package com.arq.currencyconverter.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthInputValidatorTest {

    @Test
    fun `validateSignup returns Valid for correct inputs`() {
        val result = AuthInputValidator.validateSignup(
            "John Doe",
            "john@example.com",
            "password123"
        )
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `validateSignup returns Invalid when name is too short`() {
        val result = AuthInputValidator.validateSignup("J", "john@example.com", "password123")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals(
            "Name must be at least 2 characters",
            (result as ValidationResult.Invalid).fieldErrors[AuthField.Name]
        )
    }

    @Test
    fun `validateSignup returns Invalid when email is malformed`() {
        val result = AuthInputValidator.validateSignup("John Doe", "invalid-email", "password123")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals(
            "Enter a valid email address",
            (result as ValidationResult.Invalid).fieldErrors[AuthField.Email]
        )
    }

    @Test
    fun `validateSignup returns Invalid when password is too short`() {
        val result = AuthInputValidator.validateSignup("John Doe", "john@example.com", "12345")
        assertTrue(result is ValidationResult.Invalid)
        assertEquals(
            "Password must be at least 6 characters",
            (result as ValidationResult.Invalid).fieldErrors[AuthField.Password]
        )
    }

    @Test
    fun `validateSignin returns Valid for correct inputs`() {
        val result = AuthInputValidator.validateSignin("john@example.com", "password123")
        assertEquals(ValidationResult.Valid, result)
    }
}
