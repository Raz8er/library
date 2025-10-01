package com.library.backend.service.bookreport

import com.library.backend.entity.BookReportEntity
import com.library.backend.repository.BookReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class BookReportService(
    private val bookReportRepository: BookReportRepository,
) {
    fun getLatestGeneratedAt(): LocalDateTime? = bookReportRepository.findLatestGeneratedAt()

    @Transactional
    fun createBookReport(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        isbns: Collection<String>,
    ): BookReportEntity =
        bookReportRepository.save(
            BookReportEntity(
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
                isbns = isbns.toMutableList(),
            ),
        )
}
