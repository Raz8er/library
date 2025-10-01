package com.library.backend.service.bookreport

import com.library.backend.dto.bookreport.BookReportDTO
import com.library.backend.mapper.BookReportMapper.toDTO
import com.library.backend.service.book.BookService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookReportGenerationService(
    private val bookReportService: BookReportService,
    private val bookService: BookService,
) {
    fun generateBookReport(): BookReportDTO {
        val fromDateTime = bookReportService.getLatestGeneratedAt() ?: LocalDateTime.now().minusHours(1)
        val toDateTime = LocalDateTime.now()
        val latestPublishedIsbns = bookService.getBookIsbnsByPublishingDateTimeBetween(fromDateTime, toDateTime)
        return bookReportService.createBookReport(fromDateTime, toDateTime, latestPublishedIsbns).toDTO()
    }
}
