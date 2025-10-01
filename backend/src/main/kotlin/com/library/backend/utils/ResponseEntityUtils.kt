package com.library.backend.utils

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity

object ResponseEntityUtils {
    fun <T> createResponse(
        body: T,
        statusCode: HttpStatusCode,
    ): ResponseEntity<T> = ResponseEntity(body, statusCode)
}
