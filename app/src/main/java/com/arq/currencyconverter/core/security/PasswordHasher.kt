package com.arq.currencyconverter.core.security

import java.security.MessageDigest

object PasswordHasher {
    const val SALT = "arq2026"

    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest("$password$SALT".toByteArray())
        return hashBytes.joinToString("") { byte -> "%02x".format(byte) }
    }
}
