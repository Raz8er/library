package com.library.backend.repository

import com.library.backend.RepositoryTestBase
import com.library.backend.creator.TestCreator
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDate

class AuthorRepositoryTest : RepositoryTestBase() {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var creator: TestCreator

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
        assertPage(author2.name!!, 3, firstPage)

        pageRequest = PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val secondPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertPage(author3.name!!, 0, secondPage)

        pageRequest = PageRequest.of(2, 1, Sort.by(Sort.Direction.DESC, "dateOfBirth"))
        val lastPage = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageRequest)
        assertPage(author1.name!!, 1, lastPage)
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

    private fun assertPage(
        authorName: String,
        numberOfPublishedBooks: Long,
        page: Page<AuthorWithPublishedBooksProjection>,
    ) {
        assertThat(page.totalPages).isEqualTo(3)
        assertThat(page.content).hasSize(1)
        assertThat(page.content.first().name).isEqualTo(authorName)
        assertThat(page.content.first().numberOfPublishedBooks).isEqualTo(numberOfPublishedBooks)
    }
}
