package com.library.backend.dto.author

import com.fasterxml.jackson.annotation.JsonFormat
import com.library.backend.utils.DateTimeUtils
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AuthorCreateDTO(
    @field:NotBlank
    var name: String?,
    @field:NotNull
    @field:JsonFormat(pattern = DateTimeUtils.DATE_FORMAT)
    @field:Schema(example = "25-09-2025", type = "string", format = "date")
    var dateOfBirth: LocalDate?,
)
