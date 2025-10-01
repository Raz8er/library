package com.library.backend.dto.author

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

data class AuthorWithPublishedBooksDTO(
    var id: Long? = null,
    var name: String? = null,
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    @field:Schema(example = "25-09-2025", type = "string", format = "date")
    var dateOfBirth: LocalDate? = null,
    @field:JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @field:Schema(example = "25-09-2025 11:12:13", type = "string", format = "datetime")
    var createdAt: LocalDateTime? = null,
    var numberOfPublishedBooks: Int? = null,
)
