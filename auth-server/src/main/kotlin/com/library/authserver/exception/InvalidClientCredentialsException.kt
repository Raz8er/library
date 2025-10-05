package com.library.authserver.exception

import org.springframework.http.HttpStatus

data class InvalidClientCredentialsException(
    override val message: String = "Invalid client credentials",
    val status: HttpStatus = HttpStatus.UNAUTHORIZED,
) : RuntimeException(message)
