package com.library.authserver.auth

import com.library.authserver.exception.InvalidClientCredentialsException
import com.library.authserver.repository.InMemoryClientRepository
import com.library.authserver.utils.ClientSecretUtils
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class ClientAuthenticationProvider(
    private val clientRepository: InMemoryClientRepository,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val clientId = authentication.name
        val clientSecret = authentication.credentials.toString()
        val existingClient = clientRepository.findByClientId(clientId) ?: throw InvalidClientCredentialsException()
        val storedClientSecret = existingClient.clientSecret!!
        if (!ClientSecretUtils.isClientSecretValid(clientSecret, storedClientSecret)) {
            throw InvalidClientCredentialsException()
        }
        return UsernamePasswordAuthenticationToken(clientId, null, emptyList())
    }

    override fun supports(authentication: Class<*>?): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}
