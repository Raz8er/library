package com.library.authserver.service

import com.library.authserver.dto.token.TokenResponseDTO
import com.library.authserver.dto.token.TokenScope
import com.library.authserver.exception.InvalidClientCredentialsException
import com.library.authserver.utils.ClaimsBuilder
import com.library.authserver.utils.Constants
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.util.Base64

@Service
class TokenService(
    private val jwkSource: JWKSource<SecurityContext>,
    private val jwtEncoder: JwtEncoder,
    private val clientService: ClientService,
) {
    fun generateToken(
        authorization: String,
        scope: TokenScope?,
    ): TokenResponseDTO {
        val decoded = String(Base64.getDecoder().decode(authorization.removePrefix("Basic ")))
        val (clientId, clientSecret) = decoded.split(":")
        if (!clientService.isValidClient(clientId, clientSecret)) {
            throw InvalidClientCredentialsException()
        }

        val claims = ClaimsBuilder.buildClaims(scope, clientId)
        val jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims))
        return TokenResponseDTO(
            accessToken = jwt.tokenValue,
            scope = scope?.value?.ifBlank { null },
            tokenType = Constants.TOKEN_TYPE,
            expiresIn = 600,
        )
    }

    fun getJwks(): Map<String, Any> {
        val jwkSet = (jwkSource as ImmutableJWKSet<SecurityContext>).jwkSet
        return jwkSet.toJSONObject()
    }
}
