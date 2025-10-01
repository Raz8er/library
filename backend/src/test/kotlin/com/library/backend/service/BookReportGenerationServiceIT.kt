package com.library.backend.service

import com.library.backend.IntegrationTestBase
import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import com.library.backend.repository.BookRepository
import com.library.backend.service.bookreport.BookReportGenerationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.time.LocalDateTime

class BookReportGenerationServiceIT : IntegrationTestBase() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var bookReportGenerationService: BookReportGenerationService

    @Test
    fun `should generate a book report`() {
        val author1 = AuthorEntity(name = "John Doe", dateOfBirth = LocalDate.of(1980, 1, 1))
        val author2 = AuthorEntity(name = "Jane Doe", dateOfBirth = LocalDate.of(1970, 1, 1))
        val author3 = AuthorEntity(name = "Jonas Doe", dateOfBirth = LocalDate.of(1955, 1, 1))

        val book1 =
            BookEntity(
                title = "Book 1",
                isbn = "978-3-1025-4885-4",
                genre = BookGenre.ROMANCE,
                creationDateTime = LocalDateTime.now().minusDays(252),
                publishingDateTime = LocalDateTime.now().minusDays(120),
            )
        listOf(author1, author2).forEach { book1.addAuthor(it) }
        book1.addAuthor(author1)
        book1.addAuthor(author2)
        val book2 =
            BookEntity(
                title = "Book 2",
                isbn = "978-1-6710-0204-3",
                genre = BookGenre.THRILLER,
                creationDateTime = LocalDateTime.now().minusDays(43),
                publishingDateTime = LocalDateTime.now().minusMinutes(32),
            )
        book2.addAuthor(author1)
        val book3 =
            BookEntity(
                title = "Book 3",
                isbn = "978-9-1998-9486-7",
                genre = BookGenre.SCI_FI,
                creationDateTime = LocalDateTime.now().minusDays(25),
                publishingDateTime = LocalDateTime.now().minusMinutes(12),
            )
        listOf(author1, author2, author3).forEach { book3.addAuthor(it) }

        bookRepository.saveAll(listOf(book1, book2, book3))

        val bookReport = bookReportGenerationService.generateBookReport()
        assertThat(bookReport.isbns).hasSize(2)
        assertThat(bookReport.isbns).contains(book2.isbn, book3.isbn)
    }
}
