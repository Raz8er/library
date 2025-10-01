package com.library.backend.service.bookreport

import com.library.backend.dto.bookreport.BookReportDTO

interface BookReportSendingService {
    fun sendBookReport(bookReport: BookReportDTO)
}
