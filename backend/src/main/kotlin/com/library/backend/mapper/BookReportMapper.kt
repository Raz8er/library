package com.library.backend.mapper

import com.library.backend.dto.bookreport.BookReportDTO
import com.library.backend.entity.BookReportEntity

object BookReportMapper {
    fun BookReportEntity.toDTO(): BookReportDTO =
        BookReportDTO(
            id = this.id!!,
            generatedAt = this.generatedAt!!,
            fromDateTime = this.fromDateTime!!,
            toDateTime = this.toDateTime!!,
            isbns = this.isbns,
        )
}
