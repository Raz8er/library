package com.library.backend.creator

import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import com.library.backend.repository.BookRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.UUID

@Component
@Transactional
class BookCreator(
    private val bookRepository: BookRepository,
    private val entityManager: EntityManager,
) {
    fun create(
        title: String? = null,
        isbn: String? = null,
        genre: BookGenre? = null,
        creationDateTime: LocalDateTime? = null,
        publishingDateTime: LocalDateTime? = null,
    ): BookEntity {
        val book = createBook(title, isbn, genre, creationDateTime, publishingDateTime)
        return create(book)
    }

    fun createWithAuthor(
        title: String? = null,
        isbn: String? = null,
        genre: BookGenre? = null,
        creationDateTime: LocalDateTime? = null,
        publishingDateTime: LocalDateTime? = null,
        author: AuthorEntity,
    ): BookEntity {
        val book = createBook(title, isbn, genre, creationDateTime, publishingDateTime)
        book.addAuthor(author)
        return create(book)
    }

    fun createWithAuthors(
        title: String? = null,
        isbn: String? = null,
        genre: BookGenre? = null,
        creationDateTime: LocalDateTime? = null,
        publishingDateTime: LocalDateTime? = null,
        authors: MutableSet<AuthorEntity> = mutableSetOf(),
    ): BookEntity {
        val book = createBook(title, isbn, genre, creationDateTime, publishingDateTime)
        authors.forEach { book.addAuthor(it) }
        return create(book)
    }

    fun create(book: BookEntity): BookEntity {
        val managedAuthors = book.authors.map { entityManager.merge(it) }.toMutableSet()
        book.authors.clear()
        book.authors.addAll(managedAuthors)
        return bookRepository.save(book)
    }

    private fun createBook(
        title: String?,
        isbn: String?,
        genre: BookGenre?,
        creationDateTime: LocalDateTime?,
        publishingDateTime: LocalDateTime?,
    ): BookEntity =
        BookEntity(
            title = title ?: "Test Title-${UUID.randomUUID()}",
            isbn = isbn ?: BookISBNGenerator.generateISBN(),
            genre = genre ?: BookGenreGenerator.generateBookGenre(),
            creationDateTime = creationDateTime ?: LocalDateTime.now().minusYears(SecureRandom().nextInt(100).toLong()),
            publishingDateTime =
                publishingDateTime ?: LocalDateTime
                    .now()
                    .minusYears(SecureRandom().nextInt(100).toLong()),
            authors = mutableSetOf(),
        )
}
