package com.library.authserver.repository

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryClientRepository : RegisteredClientRepository {
    private val clients: MutableMap<String, RegisteredClient> = ConcurrentHashMap()

    override fun save(registeredClient: RegisteredClient) {
        clients[registeredClient.clientId] = registeredClient
    }

    override fun findById(id: String?): RegisteredClient? = clients.values.find { it.id == id }

    override fun findByClientId(clientId: String?): RegisteredClient? = clients[clientId]
}
