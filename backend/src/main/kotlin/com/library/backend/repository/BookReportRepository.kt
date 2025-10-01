package com.library.backend.repository

import com.library.backend.entity.BookReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookReportRepository : JpaRepository<BookReportEntity, Long> {
    @Query("SELECT br.generatedAt FROM BookReportEntity br ORDER BY br.generatedAt DESC LIMIT 1")
    fun findLatestGeneratedAt(): LocalDateTime?
}
