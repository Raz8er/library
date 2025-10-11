package com.library.backend.service.author

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import com.library.backend.event.service.EventNotifier
import com.library.backend.mapper.AuthorMapper.toEntity
import com.library.backend.repository.AuthorRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

@ExtendWith(MockKExtension::class)
class AuthorServiceTest {
    @MockK
    private lateinit var authorRepository: AuthorRepository

    @RelaxedMockK
    private lateinit var authorCacheService: AuthorCacheService

    @RelaxedMockK
    private lateinit var eventNotifier: EventNotifier

    @InjectMockKs
    private lateinit var authorService: AuthorService

    @Test
    fun `should create author`() {
        val dto = AuthorCreateDTO(name = "John Doe", dateOfBirth = LocalDate.now().minusDays(125))
        val authorToSave = dto.toEntity()
        authorToSave.id == 10L

        every { authorRepository.save(any()) } returns authorToSave

        val savedAuthor = authorService.createAuthor(dto)

        assertThat(savedAuthor).usingRecursiveComparison().isEqualTo(authorToSave)
    }

    @Test
    fun `should throw exception when updating non-existing author`() {
        val nonExistingId = -1L
        val dto = AuthorUpdateDTO()
        every { authorRepository.findById(any()) } throws EntityNotFoundException("Author with id $nonExistingId not found")

        assertThrows<EntityNotFoundException> { authorService.updateAuthor(nonExistingId, dto) }
    }

    @Test
    fun `should update author`() {
        val existingAuthor = AuthorEntity(name = "John Doe", dateOfBirth = LocalDate.now().minusDays(125))
        existingAuthor.id = 10L
        val dto = AuthorUpdateDTO(name = "John Doe2", dateOfBirth = LocalDate.now().minusDays(125))
        val authorToUpdate = dto.toEntity()
        every { authorRepository.findById(any()) } returns Optional.of(existingAuthor)
        every { authorRepository.save(any()) } returns authorToUpdate

        val updatedAuthor = authorService.updateAuthor(existingAuthor.id!!, dto)

        verify(exactly = 1) { eventNotifier.publishAuthorEvent(existingAuthor.id!!) }
        assertThat(updatedAuthor).usingRecursiveComparison().isEqualTo(authorToUpdate)
    }

    @Test
    fun `should get paged authors from database`() {
        val authorsWithPublishedBooks =
            listOf(
                AuthorWithPublishedBooksProjection(
                    1L,
                    "John Doe",
                    LocalDate.now().minusDays(125),
                    ZonedDateTime.now().minusDays(150),
                    25L,
                ),
                AuthorWithPublishedBooksProjection(
                    2L,
                    "Jane Doe",
                    LocalDate.now().minusDays(130),
                    ZonedDateTime.now().minusDays(20),
                    50L,
                ),
                AuthorWithPublishedBooksProjection(
                    3L,
                    "Johnny Doe",
                    LocalDate.now().minusDays(200),
                    ZonedDateTime.now().minusDays(15),
                    12L,
                ),
            )
        val pageable = PageRequest.of(0, 10)
        val expectedAuthorPage = PageImpl(authorsWithPublishedBooks, pageable, authorsWithPublishedBooks.size.toLong())
        every { authorRepository.findAuthorsWithNumberOfPublishedBooks(any()) } returns expectedAuthorPage
        every { authorCacheService.getCachedAuthorWithPublishedBooks(any()) } returns null

        val authorPage = authorService.getAuthors(pageable)
        assertThat(authorPage).usingRecursiveComparison().isEqualTo(expectedAuthorPage)
        verify(exactly = 3) { authorCacheService.getCachedAuthorWithPublishedBooks(any()) }
    }

    @Test
    fun `should get paged authors from cache`() {
        val author1 =
            AuthorWithPublishedBooksProjection(
                1L,
                "John Doe",
                LocalDate.now().minusDays(125),
                ZonedDateTime.now().minusDays(150),
                25L,
            )
        val author2 =
            AuthorWithPublishedBooksProjection(
                2L,
                "Jane Doe",
                LocalDate.now().minusDays(130),
                ZonedDateTime.now().minusDays(20),
                50L,
            )
        val author3 =
            AuthorWithPublishedBooksProjection(
                3L,
                "Johnny Doe",
                LocalDate.now().minusDays(200),
                ZonedDateTime.now().minusDays(15),
                12L,
            )
        val authorsWithPublishedBooks = listOf(author1, author2, author3)
        val pageable = PageRequest.of(0, 10)
        val expectedAuthorPage = PageImpl(authorsWithPublishedBooks, pageable, authorsWithPublishedBooks.size.toLong())
        every { authorRepository.findAuthorsWithNumberOfPublishedBooks(any()) } returns expectedAuthorPage
        every { authorCacheService.getCachedAuthorWithPublishedBooks(any()) } returns author1 andThen author2 andThen author3

        val authorPage = authorService.getAuthors(pageable)
        assertThat(authorPage).usingRecursiveComparison().isEqualTo(expectedAuthorPage)
        verify(exactly = 3) { authorCacheService.getCachedAuthorWithPublishedBooks(any()) }
    }
}
