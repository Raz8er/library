package com.library.authserver.utils

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.security.SecureRandom
import java.util.Base64

object ClientSecretUtils {
    private const val NUMBER_OF_BYTES = 32
    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    fun generateClientSecret(): String = Base64.getEncoder().encodeToString(SecureRandom().generateSeed(NUMBER_OF_BYTES))

    fun hashClientSecret(secret: String): String = passwordEncoder.encode(secret)

    fun isClientSecretValid(
        clientSecretRaw: String,
        clientSecretEncoded: String,
    ): Boolean = passwordEncoder.matches(clientSecretRaw, clientSecretEncoded)
}
