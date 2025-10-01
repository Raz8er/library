package com.library.authserver.repository

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient

data class StoredRegisteredClient(
    val client: RegisteredClient,
    val rawSecret: String,
)
