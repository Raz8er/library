package com.library.authserver.dto.client

import jakarta.validation.constraints.NotBlank

data class ClientRequestDTO(
    @field:NotBlank
    val clientId: String?,
)
