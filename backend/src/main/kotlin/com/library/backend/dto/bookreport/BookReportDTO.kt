package com.library.backend.dto.bookreport

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class BookReportDTO(
    val id: Long,
    @field:JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    val generatedAt: LocalDateTime,
    @field:JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    val fromDateTime: LocalDateTime,
    @field:JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    val toDateTime: LocalDateTime,
    val isbns: List<String> = listOf(),
)
