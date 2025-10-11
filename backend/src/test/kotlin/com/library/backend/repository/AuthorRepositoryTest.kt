package com.library.backend.repository

import com.library.backend.creator.TestCreator
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import com.library.backend.testbase.RepositoryTestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate
import java.time.ZonedDateTime

class AuthorRepositoryTest : RepositoryTestBase() {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var creator: TestCreator

    @BeforeEach
    fun setUp() {
        creator.author().deleteAll()
    }

    @Test
    fun `should find authors with number of published books`() {
        val book1 = creator.book().create(title = "Book1")
        val book2 = creator.book().create(title = "Book2")
        val book3 = creator.book().create(title = "Book3")
        val author1 =
            creator
                .author()
                .createWithBook(name = "Author1", dateOfBirth = LocalDate.now().minusYears(55), book = book2)
        val author2 =
            creator
                .author()
                .createWithBooks(
                    name = "Author2",
                    dateOfBirth = LocalDate.now().minusYears(35),
                    books = mutableSetOf(book1, book2, book3),
                )
        val author3 = creator.author().create(name = "Author3", dateOfBirth = LocalDate.now().minusYears(45))

        var pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val firstPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertPage(3, author2.name!!, 3, firstPage)

        pageRequest = PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val secondPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertPage(3, author3.name!!, 0, secondPage)

        pageRequest = PageRequest.of(2, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val lastPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertPage(3, author1.name!!, 1, lastPage)

        pageRequest = PageRequest.of(3, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val emptyPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertEmptyPage(3, emptyPage)
    }

    @Test
    fun `should find single author with number of published books`() {
        val book1 = creator.book().create(title = "Book1")
        val book2 = creator.book().create(title = "Book2")
        val book3 = creator.book().create(title = "Book3")
        val author = creator.author().createWithBooks(name = "Author1", books = mutableSetOf(book1, book2, book3))

        val result = authorRepository.findAuthorWithNumberOfPublishedBooks(author.id!!)
        assertThat(result).isNotNull
        assertThat(result!!.name).isEqualTo(author.name)
        assertThat(result.numberOfPublishedBooks).isEqualTo(3)
    }

    @Test
    fun `should find authors by createdAt cursor`() {
        val book1 = creator.book().create(title = "Book1")
        val book2 = creator.book().create(title = "Book2")
        val book3 = creator.book().create(title = "Book3")
        creator
            .author()
            .createWithBook(
                name = "Author1",
                dateOfBirth = LocalDate.now().minusYears(55),
                createdAt = ZonedDateTime.now().minusDays(35),
                book = book2,
            )
        val author2 =
            creator
                .author()
                .createWithBooks(
                    name = "Author2",
                    dateOfBirth = LocalDate.now().minusYears(35),
                    createdAt = ZonedDateTime.now().minusDays(55),
                    books = mutableSetOf(book1, book2, book3),
                )
        val author3 =
            creator.author().create(
                name = "Author3",
                dateOfBirth = LocalDate.now().minusYears(45),
                createdAt = ZonedDateTime.now().minusDays(70),
            )

        var pageRequest = PageRequest.of(0, 1)
        val firstPage =
            authorRepository.findAuthorsByCreatedAtCursor(
                createdAt = ZonedDateTime.now().minusDays(50),
                authorId = Long.MAX_VALUE,
                pageRequest,
            )
        assertPage(2, author2.name!!, 3, firstPage)

        pageRequest = PageRequest.of(1, 1)
        val secondPage =
            authorRepository.findAuthorsByCreatedAtCursor(
                createdAt = ZonedDateTime.now().minusDays(50),
                authorId = Long.MAX_VALUE,
                pageRequest,
            )
        assertPage(2, author3.name!!, 0, secondPage)

        pageRequest = PageRequest.of(2, 1)
        val emptyPage =
            authorRepository.findAuthorsByCreatedAtCursor(
                createdAt = ZonedDateTime.now().minusDays(50),
                authorId = Long.MAX_VALUE,
                pageRequest,
            )
        assertEmptyPage(2, emptyPage)
    }

    @Test
    fun `should find authors by numberOfPublishedBooks cursor`() {
        val book1 = creator.book().create(title = "Book1")
        val book2 = creator.book().create(title = "Book2")
        val book3 = creator.book().create(title = "Book3")
        val author1 =
            creator
                .author()
                .createWithBook(
                    name = "Author1",
                    dateOfBirth = LocalDate.now().minusYears(55),
                    createdAt = ZonedDateTime.now().minusDays(35),
                    book = book2,
                )
        creator
            .author()
            .createWithBooks(
                name = "Author2",
                dateOfBirth = LocalDate.now().minusYears(35),
                createdAt = ZonedDateTime.now().minusDays(55),
                books = mutableSetOf(book1, book2, book3),
            )
        creator.author().create(
            name = "Author3",
            dateOfBirth = LocalDate.now().minusYears(45),
            createdAt = ZonedDateTime.now().minusDays(70),
        )

        var pageRequest = PageRequest.of(0, 1)
        val firstPage =
            authorRepository.findAuthorsByNumberOfPublishedBooksCursor(
                authorId = Long.MAX_VALUE,
                numberOfPublishedBooks = 2,
                pageable = pageRequest,
            )
        assertPage(1, author1.name!!, 1, firstPage)

        pageRequest = PageRequest.of(1, 1)
        val emptyPage =
            authorRepository.findAuthorsByNumberOfPublishedBooksCursor(
                authorId = Long.MAX_VALUE,
                numberOfPublishedBooks = 2,
                pageable = pageRequest,
            )
        assertEmptyPage(1, emptyPage)
    }

    private fun assertPage(
        totalPages: Int,
        authorName: String,
        numberOfPublishedBooks: Long,
        page: Page<AuthorWithPublishedBooksProjection>,
    ) {
        assertThat(page.totalPages).isEqualTo(totalPages)
        assertThat(page.content).hasSize(1)
        assertThat(page.content.first().name).isEqualTo(authorName)
        assertThat(page.content.first().numberOfPublishedBooks).isEqualTo(numberOfPublishedBooks)
    }

    private fun assertEmptyPage(
        totalPages: Int,
        page: Page<AuthorWithPublishedBooksProjection>,
    ) {
        assertThat(page.totalPages).isEqualTo(totalPages)
        assertThat(page.content).hasSize(0)
    }
}
