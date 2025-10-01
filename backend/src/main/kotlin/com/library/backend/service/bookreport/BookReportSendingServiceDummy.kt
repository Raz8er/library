package com.library.backend.service.bookreport

import com.library.backend.dto.bookreport.BookReportDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BookReportSendingServiceDummy : BookReportSendingService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun sendBookReport(bookReport: BookReportDTO) {
        logger.info("Book report ${bookReport.id} sent")
    }
}
