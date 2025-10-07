package com.library.backend.creator

import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import com.library.backend.repository.AuthorRepository
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDate

@Component
class AuthorCreator(
    private val authorRepository: AuthorRepository,
) {
    fun create(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth)
        return create(author)
    }

    fun createWithBook(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
        book: BookEntity,
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth)
        author.addBook(book)
        return create(author)
    }

    fun createWithBooks(
        name: String? = null,
        dateOfBirth: LocalDate? = null,
        books: MutableSet<BookEntity> = mutableSetOf(),
    ): AuthorEntity {
        val author = createAuthor(name, dateOfBirth)
        books.forEach { author.addBook(it) }
        return create(author)
    }

    fun create(author: AuthorEntity): AuthorEntity = authorRepository.save(author)

    private fun createAuthor(
        name: String?,
        dateOfBirth: LocalDate?,
    ): AuthorEntity =
        AuthorEntity(
            name = name ?: "Test Author-${SecureRandom().nextInt()}",
            dateOfBirth = dateOfBirth ?: LocalDate.now().minusYears(SecureRandom().nextInt(100).toLong()),
            books = mutableSetOf(),
        )
}
