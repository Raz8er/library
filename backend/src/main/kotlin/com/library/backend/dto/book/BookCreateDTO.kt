package com.library.backend.dto.book

import com.fasterxml.jackson.annotation.JsonFormat
import com.library.backend.dto.validation.ValidBookGenre
import com.library.backend.utils.DateTimeUtils
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class BookCreateDTO(
    @field:NotBlank
    var title: String?,
    @field:NotBlank
    @field:Size(max = 17)
    @field:Pattern("([0-9]){3}-([0-9]){2}-([0-9]){5}-([0-9]){2}-([0-9])")
    var isbn: String?,
    @field:NotNull
    @field:ValidBookGenre
    var genre: String?,
    @field:NotNull
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    @field:Schema(example = "25-09-2025 11:12:13", type = "string", format = "datetime")
    var creationDateTime: LocalDateTime?,
    @field:NotEmpty
    var authorIds: MutableSet<Long>? = mutableSetOf(),
)
