package com.library.authserver.exception

import org.springframework.http.HttpStatus

data class InvalidGrantTypeException(
    override val message: String = "Unsupported grant_type",
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
) : RuntimeException(message)
