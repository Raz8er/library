package com.library.backend.dto.bookreport

import com.fasterxml.jackson.annotation.JsonFormat
import com.library.backend.utils.DateTimeUtils
import java.time.LocalDateTime

data class BookReportDTO(
    val id: Long,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    val generatedAt: LocalDateTime,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    val fromDateTime: LocalDateTime,
    @field:JsonFormat(pattern = DateTimeUtils.DATE_TIME_FORMAT)
    val toDateTime: LocalDateTime,
    val isbns: List<String> = listOf(),
)
