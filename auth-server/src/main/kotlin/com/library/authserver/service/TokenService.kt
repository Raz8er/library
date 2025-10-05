package com.library.authserver.service

import com.library.authserver.dto.token.TokenRequestDTO
import com.library.authserver.dto.token.TokenResponseDTO
import com.library.authserver.exception.InvalidClientCredentialsException
import com.library.authserver.exception.InvalidGrantTypeException
import com.library.authserver.utils.ClaimsBuilder
import com.library.authserver.utils.Constants
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwkSource: JWKSource<SecurityContext>,
    private val jwtEncoder: JwtEncoder,
    private val clientService: ClientService,
) {
    fun generateToken(tokenRequest: TokenRequestDTO): TokenResponseDTO {
        if (tokenRequest.grantType != AuthorizationGrantType.CLIENT_CREDENTIALS.value) {
            throw InvalidGrantTypeException()
        }
        val clientId = tokenRequest.clientId
        val clientSecret = tokenRequest.clientSecret
        if (!clientService.isValidClient(clientId, clientSecret)) {
            throw InvalidClientCredentialsException()
        }
        val scope = tokenRequest.scope
        val claims = ClaimsBuilder.buildClaims(scope, clientId, tokenRequest.audience)
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
