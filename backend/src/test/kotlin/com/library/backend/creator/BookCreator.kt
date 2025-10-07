package com.library.backend.creator

import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import java.security.SecureRandom
import java.time.LocalDateTime

class BookCreator {
    private val book =
        BookEntity(
            title = "Test Title",
            isbn = BookISBNGenerator.generateISBN(),
            genre = BookGenreGenerator.generateBookGenre(),
            creationDateTime = LocalDateTime.now().minusYears(SecureRandom().nextInt(100).toLong()),
            publishingDateTime = LocalDateTime.now().minusYears(SecureRandom().nextInt(100).toLong()),
            authors = mutableSetOf(),
        )

    fun withTitle(title: String?) = apply { this.book.title = title }

    fun withIsbn(isbn: String?) = apply { this.book.isbn = isbn }

    fun withGenre(genre: BookGenre?) = apply { this.book.genre = genre }

    fun withCreationDateTime(creationDateTime: LocalDateTime?) = apply { this.book.creationDateTime = creationDateTime }

    fun withPublishingDateTime(publishingDateTime: LocalDateTime?) = apply { this.book.publishingDateTime = publishingDateTime }

    fun withAuthors(authors: MutableSet<AuthorEntity>) = apply { authors.forEach { this.book.addAuthor(it) } }

    fun withBook(author: AuthorEntity) =
        apply {
            this.book.addAuthor(author)
        }

    fun create(): BookEntity = book
}
