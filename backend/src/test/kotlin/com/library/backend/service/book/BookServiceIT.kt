package com.library.backend.service.book

import com.library.backend.creator.TestCreator
import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.entity.BookEntity
import com.library.backend.testbase.IntegrationTestBase
import com.library.backend.utils.BookGenreGenerator
import com.library.backend.utils.BookISBNGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class BookServiceIT : IntegrationTestBase() {
    @Autowired
    private lateinit var bookService: BookService

    @Autowired
    private lateinit var creator: TestCreator

    @BeforeEach
    fun setUp() {
        creator.book().deleteAll()
    }

    @Test
    fun `should create a book`() {
        val author1 = creator.author().create(name = "Author1")
        val author2 = creator.author().create(name = "Author2")
        val dto =
            BookCreateDTO(
                title = "Book1",
                isbn = BookISBNGenerator.generateISBN(),
                genre = BookGenreGenerator.generateBookGenre().name,
                creationDateTime = LocalDateTime.now().minusDays(55),
                authorIds = mutableSetOf(author1.id!!, author2.id!!),
            )

        val book = bookService.createBook(dto)

        assertThat(book.title).isEqualTo(dto.title)
        assertThat(book.authors).containsExactly(author1, author2)
    }

    @Test
    fun `should return the existing book when creating a book`() {
        val author1 = creator.author().create(name = "Author1")
        val author2 = creator.author().create(name = "Author2")
        val savedBook = creator.book().createWithAuthors(title = "Book1", authors = mutableSetOf(author1, author2))

        val dto =
            BookCreateDTO(
                title = savedBook.title,
                isbn = savedBook.isbn,
                genre = savedBook.genre!!.name,
                creationDateTime = savedBook.creationDateTime,
                authorIds = savedBook.authors.map { it.id!! }.toMutableSet(),
            )
        val book = bookService.createBook(dto)

        assertThat(book)
            .extracting(BookEntity::title, BookEntity::isbn)
            .containsExactly(savedBook.title, savedBook.isbn)
    }
}
