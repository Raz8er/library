package com.library.backend.dto.author

import com.fasterxml.jackson.annotation.JsonFormat
import com.library.backend.utils.DateTimeUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class AuthorDTO(
    var id: Long? = null,
    var name: String? = null,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_FORMAT)
    @field:Schema(example = "25-09-2025", type = "string", format = "date")
    var dateOfBirth: LocalDate? = null,
)
