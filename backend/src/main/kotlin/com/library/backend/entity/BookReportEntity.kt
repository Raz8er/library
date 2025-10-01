package com.library.backend.entity

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "book_reports")
class BookReportEntity(
    @Column(name = "generated_at")
    var generatedAt: LocalDateTime? = LocalDateTime.now(),
    @Column(name = "from_date_time")
    var fromDateTime: LocalDateTime? = null,
    @Column(name = "to_date_time")
    var toDateTime: LocalDateTime? = null,
    @ElementCollection
    @CollectionTable(
        name = "book_report_isbns",
        joinColumns = [JoinColumn(name = "book_report_id")],
    )
    @Column(name = "isbn")
    val isbns: MutableList<String> = mutableListOf(),
) : BaseEntity()
