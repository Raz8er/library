package com.library.authserver.api

import com.library.authserver.dto.token.TokenResponseDTO
import com.library.authserver.dto.token.TokenScope
import com.library.authserver.dto.validation.ValidScope
import com.library.authserver.service.TokenService
import com.library.authserver.utils.ResponseEntityUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class TokenController(
    private val tokenService: TokenService,
) {
    @GetMapping("/token")
    fun getToken(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam(required = false) @ValidScope scope: String?,
    ): ResponseEntity<TokenResponseDTO> =
        ResponseEntityUtils.createResponse(
            tokenService.generateToken(
                authorization,
                TokenScope.getEnumValueFrom(scope),
            ),
            HttpStatus.OK,
        )

    @GetMapping("/jwks.json")
    fun getJwks(): Map<String, Any> = tokenService.getJwks()
}
