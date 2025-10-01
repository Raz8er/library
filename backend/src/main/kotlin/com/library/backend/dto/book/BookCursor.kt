package com.library.backend.dto.book

import java.time.LocalDateTime
import java.util.Base64

data class BookCursor(
    val publishingDateTime: LocalDateTime?,
    val id: Long,
) {
    fun encode(): String =
        Base64.getEncoder().encodeToString(
            "${publishingDateTime ?: ""}|$id".toByteArray(),
        )

    companion object {
        fun decode(cursor: String): BookCursor {
            val parts = String(Base64.getDecoder().decode(cursor)).split("|")
            return BookCursor(
                publishingDateTime = parts[0].takeIf { it.isNotEmpty() }?.let { LocalDateTime.parse(it) },
                id = parts[1].toLong(),
            )
        }
    }
}
