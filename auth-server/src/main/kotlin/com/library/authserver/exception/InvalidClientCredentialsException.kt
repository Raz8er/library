package com.library.authserver.exception

import org.springframework.http.HttpStatus

data class InvalidClientCredentialsException(
    val status: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val message: String = "Invalid client credentials",
) : RuntimeException(message)
