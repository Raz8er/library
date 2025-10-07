package com.library.backend.creator

import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import java.security.SecureRandom
import java.time.LocalDate

class AuthorCreator {
    private val author =
        AuthorEntity(
            name = "Test Author-${SecureRandom().nextInt()}",
            dateOfBirth = LocalDate.now().minusYears(SecureRandom().nextInt(100).toLong()),
            books = mutableSetOf(),
        )

    fun withName(name: String?) = apply { this.author.name = name }

    fun withDateOfBirth(dateOfBirth: LocalDate?) = apply { this.author.dateOfBirth = dateOfBirth }

    fun withBooks(books: MutableSet<BookEntity>) = apply { books.forEach { this.author.addBook(it) } }

    fun withBook(book: BookEntity) =
        apply {
            this.author.addBook(book)
        }

    fun create(): AuthorEntity = author
}
