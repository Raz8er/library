package com.library.backend.entity.projection

import java.time.LocalDate
import java.time.ZonedDateTime

data class AuthorWithPublishedBooksProjection(
    val id: Long,
    val name: String,
    val dateOfBirth: LocalDate,
    val createdAt: ZonedDateTime,
    val numberOfPublishedBooks: Long,
)
