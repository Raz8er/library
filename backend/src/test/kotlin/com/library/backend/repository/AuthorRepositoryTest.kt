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
    private lateinit var bookRepository: BookRepository

    @Test
    fun `should find authors with number of published books`() {
        val author1 =
            TestCreator
                .author()
                .withName("Author1")
                .withDateOfBirth(LocalDate.now().minusYears(55))
                .create()
        val author2 =
            TestCreator
                .author()
                .withName("Author2")
                .withDateOfBirth(LocalDate.now().minusYears(35))
                .create()
        val author3 =
            TestCreator
                .author()
                .withName("Author3")
                .withDateOfBirth(LocalDate.now().minusYears(45))
                .create()
        authorRepository.save(author3)
        val book1 =
            TestCreator
                .book()
                .withTitle("Book1")
                .withAuthors(mutableSetOf(author2))
                .create()
        val book2 =
            TestCreator
                .book()
                .withTitle("Book2")
                .withAuthors(mutableSetOf(author1, author2))
                .create()
        val book3 =
            TestCreator
                .book()
                .withTitle("Book3")
                .withAuthors(mutableSetOf(author2))
                .create()
        bookRepository.saveAll(listOf(book1, book2, book3))

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
