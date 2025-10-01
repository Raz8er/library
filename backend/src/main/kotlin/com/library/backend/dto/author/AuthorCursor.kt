package com.library.backend.dto.author

import java.time.LocalDateTime
import java.util.Base64

data class AuthorCursor(
    val createdAt: LocalDateTime?,
    val numberOfPublishedBooks: Long?,
    val id: Long,
) {
    fun encode(): String =
        Base64.getEncoder().encodeToString(
            "${createdAt ?: ""}|${numberOfPublishedBooks ?: ""}|$id".toByteArray(),
        )

    companion object {
        fun decode(cursor: String): AuthorCursor {
            val parts = String(Base64.getDecoder().decode(cursor)).split("|")
            return AuthorCursor(
                createdAt = parts[0].takeIf { it.isNotEmpty() }?.let { LocalDateTime.parse(it) },
                numberOfPublishedBooks = parts[1].takeIf { it.isNotEmpty() }?.toLong(),
                id = parts[2].toLong(),
            )
        }
    }
}
