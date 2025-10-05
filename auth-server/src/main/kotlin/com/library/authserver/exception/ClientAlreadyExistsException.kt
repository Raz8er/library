package com.library.authserver.exception

import org.springframework.http.HttpStatus

data class ClientAlreadyExistsException(
    override val message: String = "Client already exists",
    val status: HttpStatus = HttpStatus.CONFLICT,
) : RuntimeException(message)
