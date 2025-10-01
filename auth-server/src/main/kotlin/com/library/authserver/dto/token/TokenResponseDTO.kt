package com.library.authserver.dto.token

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TokenResponseDTO(
    val accessToken: String,
    val scope: String?,
    val tokenType: String,
    val expiresIn: Int,
)
