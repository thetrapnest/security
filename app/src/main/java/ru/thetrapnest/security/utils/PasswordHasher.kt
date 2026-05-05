package ru.thetrapnest.security.utils

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {
    private const val SALT_SIZE = 16

    fun generateSalt(): String {
        val salt = ByteArray(SALT_SIZE)
        SecureRandom().nextBytes(salt)
        return salt.toHex()
    }

    fun hashPassword(password: String, saltHex: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest((saltHex + password).toByteArray())
        return hash.toHex()
    }

    fun matches(password: String, saltHex: String, expectedHash: String): Boolean {
        return hashPassword(password, saltHex) == expectedHash
    }

    private fun ByteArray.toHex(): String {
        return joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
