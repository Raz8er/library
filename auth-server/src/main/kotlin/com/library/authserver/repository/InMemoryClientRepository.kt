package com.library.authserver.repository

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryClientRepository : RegisteredClientRepository {
    private val clients: MutableMap<String, StoredRegisteredClient> = ConcurrentHashMap()

    fun saveWithSecret(
        client: RegisteredClient,
        rawSecret: String,
    ) {
        val storedClient = StoredRegisteredClient(client, rawSecret)
        clients[client.clientId] = storedClient
    }

    override fun save(registeredClient: RegisteredClient?): Unit = throw UnsupportedOperationException("Use saveWithSecret instead")

    override fun findById(id: String?): RegisteredClient? = clients.values.firstOrNull { it.client.id == id }?.client

    override fun findByClientId(clientId: String?): RegisteredClient? = clients[clientId]?.client

    fun findWithSecret(clientId: String): StoredRegisteredClient? = clients[clientId]
}
