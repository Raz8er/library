package com.library.authserver.dto.token

data class TokenRequestDTO(
    val clientId: String,
    val clientSecret: String,
    val grantType: String,
    val audience: String?,
    val scope: TokenScope?,
)
