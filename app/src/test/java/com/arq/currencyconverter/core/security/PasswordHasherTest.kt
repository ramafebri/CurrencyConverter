package com.arq.currencyconverter.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun `hash returns consistent result for same password`() {
        val password = "secret_password"
        val hash1 = PasswordHasher.hash(password)
        val hash2 = PasswordHasher.hash(password)

        assertEquals(hash1, hash2)
    }

    @Test
    fun `hash returns different results for different passwords`() {
        val hash1 = PasswordHasher.hash("password123")
        val hash2 = PasswordHasher.hash("password124")

        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hash length is consistent`() {
        val hash = PasswordHasher.hash("any_password")
        // SHA-256 in hex is 64 characters
        assertEquals(64, hash.length)
    }
}
