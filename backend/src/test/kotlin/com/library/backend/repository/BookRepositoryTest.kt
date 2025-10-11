package com.library.backend.repository

import com.library.backend.creator.TestCreator
import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.BookEntity
import com.library.backend.testbase.RepositoryTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class BookRepositoryTest : RepositoryTestBase() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var creator: TestCreator

    @BeforeEach
    fun setUp() {
        creator.book().deleteAll()
    }

    @Test
    fun `should find book isbns by publishing datetime`() {
        creator.book().create(title = "Book1", publishingDateTime = LocalDateTime.now().minusMonths(7))
        val book2 = creator.book().create(title = "Book2", publishingDateTime = LocalDateTime.now().minusMonths(3))
        val book3 = creator.book().create(title = "Book3", publishingDateTime = LocalDateTime.now().minusMonths(4))

        val startDateTime = LocalDateTime.now().minusMonths(5)
        val endDateTime = LocalDateTime.now()
        val bookIsbns = bookRepository.findBookIsbnsByPublishingDateTimeBetween(startDateTime, endDateTime)

        assertThat(bookIsbns.size).isEqualTo(2)
        assertThat(bookIsbns).contains(book2.isbn, book3.isbn)
    }

    @Test
    fun `should find books by filter and cursor`() {
        createBooks()

        val pageRequest: PageRequest = PageRequest.of(0, 10)
        val booksByTitle = getBooks(title = "%the lord of the rings%", pageable = pageRequest)
        assertThat(booksByTitle.content.map { it.title }).containsExactly(
            "The Lord of the Rings - The Fellowship of the Ring",
            "The Lord of the Rings - Two Towers",
        )

        val booksByIsbn = getBooks(isbn = "857-93-24718-57-6", pageable = pageRequest)
        assertThat(booksByIsbn.content.map { it.title }).containsExactly("50 Shades of Grey")

        val booksByGenre = getBooks(genre = BookGenre.FANTASY, pageable = pageRequest)
        assertThat(booksByGenre.content.map { it.title })
            .containsExactly(
                "Harry Potter",
                "The Lord of the Rings - The Fellowship of the Ring",
                "The Lord of the Rings - Two Towers",
            )

        val booksByAuthor = getBooks(author = "%author5%", pageable = pageRequest)
        assertThat(booksByAuthor.content.map { it.title })
            .containsExactly(
                "50 Shades of Grey",
                "The Lord of the Rings - Two Towers",
            )

        val booksByGenreAndAuthor = getBooks(genre = BookGenre.FANTASY, author = "%author5%", pageable = pageRequest)
        assertThat(booksByGenreAndAuthor.content.map { it.title })
            .containsExactly(
                "The Lord of the Rings - Two Towers",
            )

        val booksByGenreAndPublishingDateTime =
            getBooks(
                genre = BookGenre.FANTASY,
                publishingDateTime = LocalDateTime.now().minusDays(200),
                pageable = pageRequest,
            )
        assertThat(booksByGenreAndPublishingDateTime.content.map { it.title })
            .containsExactly(
                "The Lord of the Rings - The Fellowship of the Ring",
                "The Lord of the Rings - Two Towers",
            )
    }

    private fun getBooks(
        title: String? = null,
        isbn: String? = null,
        genre: BookGenre? = null,
        author: String? = null,
        publishingDateTime: LocalDateTime? = LocalDateTime.now(),
        id: Long? = Long.MAX_VALUE,
        pageable: Pageable,
    ): Page<BookEntity> = bookRepository.findBooksByFiltersAndCursor(title, isbn, genre, author, publishingDateTime, id, pageable)

    private fun createBooks() {
        val author1 = creator.author().create(name = "Author1")
        val author2 = creator.author().create(name = "Author2")
        val author3 = creator.author().create(name = "Author3")
        val author4 = creator.author().create(name = "Author4")
        val author5 = creator.author().create(name = "Author5")
        creator.book().createWithAuthors(
            title = "Harry Potter",
            genre = BookGenre.FANTASY,
            publishingDateTime = LocalDateTime.now().minusDays(153),
            authors = mutableSetOf(author2, author4),
        )
        creator.book().createWithAuthors(
            title = "The Lord of the Rings - The Fellowship of the Ring",
            genre = BookGenre.FANTASY,
            publishingDateTime = LocalDateTime.now().minusDays(235),
            authors = mutableSetOf(author3),
        )
        creator.book().createWithAuthors(
            title = "50 Shades of Grey",
            isbn = "857-93-24718-57-6",
            genre = BookGenre.ROMANCE,
            publishingDateTime = LocalDateTime.now().minusDays(112),
            authors = mutableSetOf(author1, author3, author4, author5),
        )
        creator.book().createWithAuthors(
            title = "All Quiet on the Western Front",
            genre = BookGenre.HISTORY,
            publishingDateTime = LocalDateTime.now().minusDays(75),
            authors = mutableSetOf(author1, author2, author3),
        )
        creator.book().createWithAuthors(
            title = "The Lord of the Rings - Two Towers",
            genre = BookGenre.FANTASY,
            publishingDateTime = LocalDateTime.now().minusDays(250),
            authors = mutableSetOf(author1, author2, author3, author4, author5),
        )
    }
}
