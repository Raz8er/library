package com.library.authserver.service

import com.library.authserver.dto.client.ClientRequestDTO
import com.library.authserver.dto.client.ClientResponseDTO
import com.library.authserver.repository.InMemoryClientRepository
import com.library.authserver.utils.PasswordGenerator
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.core.AuthorizationGrantType
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
        private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    fun registerClient(client: ClientRequestDTO): ClientResponseDTO {
        val existingClient = clientRepository.findWithSecret(client.clientId!!)
        if (existingClient != null) {
            return ClientResponseDTO(existingClient.client.clientId, existingClient.rawSecret)
        }
        val randomPassword = PasswordGenerator.generate()
        val encodedPassword = passwordEncoder.encode(randomPassword)
        val client =
            RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(client.clientId)
                .clientSecret(encodedPassword)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(10)).build())
                .build()
        clientRepository.saveWithSecret(client, randomPassword)
        return ClientResponseDTO(client.clientId, randomPassword)
    }

    fun isValidClient(
        clientId: String,
        clientSecret: String,
    ): Boolean {
        val existingClient = clientRepository.findWithSecret(clientId) ?: return false
        return passwordEncoder.matches(clientSecret, existingClient.client.clientSecret)
    }
}
