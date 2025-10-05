package com.library.authserver.service

import com.library.authserver.dto.client.ClientRequestDTO
import com.library.authserver.dto.client.ClientResponseDTO
import com.library.authserver.exception.ClientAlreadyExistsException
import com.library.authserver.exception.InvalidClientCredentialsException
import com.library.authserver.repository.InMemoryClientRepository
import com.library.authserver.utils.ClientSecretUtils
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class ClientService(
    private val clientRepository: InMemoryClientRepository,
) {
    companion object {
        private const val TOKEN_TTL_MINUTES = 10L
    }

    fun registerClient(client: ClientRequestDTO): ClientResponseDTO {
        if (clientExists(client)) {
            throw ClientAlreadyExistsException()
        }
        val clientSecret = ClientSecretUtils.generateClientSecret()
        val client =
            RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(client.clientId)
                .clientSecret(ClientSecretUtils.hashClientSecret(clientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(
                    TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(TOKEN_TTL_MINUTES)).build(),
                ).build()
        clientRepository.save(client)
        return ClientResponseDTO(client.clientId, clientSecret)
    }

    fun rotateClientSecret(
        clientId: String,
        authentication: Authentication,
    ): ClientResponseDTO {
        if (clientId != authentication.name) {
            throw InvalidClientCredentialsException()
        }
        val existingClient = clientRepository.findByClientId(clientId) ?: throw InvalidClientCredentialsException()
        val newClientSecret = ClientSecretUtils.generateClientSecret()
        val updatedClient =
            RegisteredClient
                .from(existingClient)
                .clientSecret(ClientSecretUtils.hashClientSecret(newClientSecret))
                .build()
        clientRepository.save(updatedClient)
        return ClientResponseDTO(clientId, newClientSecret)
    }

    fun isValidClient(
        clientId: String,
        clientSecret: String,
    ): Boolean {
        val existingClient = clientRepository.findByClientId(clientId) ?: return false
        val storedHashSecret = existingClient.clientSecret!!
        return ClientSecretUtils.isClientSecretValid(clientSecret, storedHashSecret)
    }

    private fun clientExists(client: ClientRequestDTO): Boolean = clientRepository.existsByClientId(client.clientId)
}
