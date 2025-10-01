package com.library.backend.dto.author

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class AuthorCreateDTO(
    @field:NotBlank
    var name: String?,
    @field:NotNull
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @field:Schema(example = "25-09-2025", type = "string", format = "date")
    var dateOfBirth: LocalDate,
)
