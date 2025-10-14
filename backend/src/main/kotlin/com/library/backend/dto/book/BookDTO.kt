package com.library.backend.dto.book

import com.fasterxml.jackson.annotation.JsonFormat
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.utils.DateTimeUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class BookDTO(
    var id: Long? = null,
    var title: String? = null,
    var isbn: String? = null,
    var genre: BookGenre? = null,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    @field:Schema(example = "25-09-2025 11:12:13", type = "string", format = "datetime")
    var creationDateTime: LocalDateTime? = null,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    @field:Schema(example = "25-09-2025 11:12:13", type = "string", format = "datetime")
    var publishingDateTime: LocalDateTime? = null,
    var authors: MutableSet<AuthorDTO> = mutableSetOf(),
)
