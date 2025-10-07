package com.library.backend.creator

import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import com.library.backend.repository.AuthorRepository
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDate
import java.time.ZonedDateTime

@Component
class AuthorCreator(
    private val authorRepository: AuthorRepository,
) {
    fun create(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
        createdAt: ZonedDateTime? = null,
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth, createdAt)
        return create(author)
    }

    fun createWithBook(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
        createdAt: ZonedDateTime? = null,
        book: BookEntity,
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth, createdAt)
        author.addBook(book)
        return create(author)
    }

    fun createWithBooks(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
        createdAt: ZonedDateTime? = null,
        books: MutableSet<BookEntity> = mutableSetOf(),
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth, createdAt)
        books.forEach { author.addBook(it) }
        return create(author)
    }

    fun create(author: AuthorEntity): AuthorEntity = authorRepository.save(author)

    private fun createAuthor(
        name: String?,
        dateOfBirth: LocalDate?,
        createdAt: ZonedDateTime?,
    ): AuthorEntity {
        val entity =
            AuthorEntity(
                name = name ?: "Test Author-${SecureRandom().nextInt()}",
                dateOfBirth = dateOfBirth ?: LocalDate.now().minusYears(SecureRandom().nextInt(100).toLong()),
                books = mutableSetOf(),
            )
        entity.createdAt = createdAt ?: ZonedDateTime.now().minusYears(SecureRandom().nextInt(100).toLong())
        return entity
    }
}
