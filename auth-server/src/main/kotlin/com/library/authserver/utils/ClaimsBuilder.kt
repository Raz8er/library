package com.library.authserver.utils

import com.library.authserver.dto.token.TokenScope
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import java.time.Instant
import java.util.UUID

object ClaimsBuilder {
    fun buildClaims(
        scope: TokenScope?,
        clientId: String,
        audience: String?,
    ): JwtClaimsSet {
        val now = Instant.now()
        return JwtClaimsSet
            .builder()
            .issuer(Constants.ISSUER)
            .audience(audience?.let { listOf(it) } ?: listOf(Constants.AUDIENCE))
            .subject(scope?.user ?: "")
            .claim(Constants.SCOPE, scope?.value ?: "")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(600))
            .id(UUID.randomUUID().toString())
            .claim(Constants.CLIENT_ID, clientId)
            .build()
    }
}
