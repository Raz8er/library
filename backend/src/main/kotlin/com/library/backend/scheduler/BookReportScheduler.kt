package com.library.backend.scheduler

import com.library.backend.service.bookreport.BookReportGenerationService
import com.library.backend.service.bookreport.BookReportSendingService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BookReportScheduler(
    private val bookReportGenerationService: BookReportGenerationService,
    private val bookReportSendingService: BookReportSendingService,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun generateReport() {
        val bookReport = bookReportGenerationService.generateBookReport()
        bookReportSendingService.sendBookReport(bookReport)
    }
}
