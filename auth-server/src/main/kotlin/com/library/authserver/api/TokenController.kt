package com.library.authserver.api

import com.library.authserver.dto.token.TokenRequestDTO
import com.library.authserver.dto.token.TokenResponseDTO
import com.library.authserver.dto.token.TokenScope
import com.library.authserver.dto.validation.ValidScope
import com.library.authserver.service.TokenService
import com.library.authserver.utils.ResponseEntityUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TokenController(
    private val tokenService: TokenService,
) {
    @PostMapping("/token", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getToken(
        @RequestParam("clientId") clientId: String,
        @RequestParam("clientSecret") clientSecret: String,
        @RequestParam("grant_type") grantType: String,
        @RequestParam("audience", required = false) audience: String?,
        @RequestParam("scope", required = false) @ValidScope scope: String?,
    ): ResponseEntity<TokenResponseDTO> =
        ResponseEntityUtils.createResponse(
            tokenService.generateToken(
                TokenRequestDTO(
                    clientId = clientId,
                    clientSecret = clientSecret,
                    grantType = grantType,
                    audience = audience,
                    scope = TokenScope.getEnumValueFrom(scope),
                ),
            ),
            HttpStatus.OK,
        )

    @GetMapping("/jwks.json")
    fun getJwks(): Map<String, Any> = tokenService.getJwks()
}
