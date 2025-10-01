package com.library.backend.dto.author

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class AuthorUpdateDTO(
    var name: String? = null,
    @field:JsonFormat(pattern = "dd-MM-yyyy")
    @field:DateTimeFormat(pattern = "dd-MM-yyyy")
    @field:Schema(example = "25-09-2025", type = "string", format = "date")
    var dateOfBirth: LocalDate? = null,
)
