package com.library.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(
    name = "authors",
    indexes = [
        Index(name = "authors_name_idx", columnList = "name"),
        Index(name = "authors_created_at_idx", columnList = "created_at"),
    ],
)
class AuthorEntity(
    @Column(name = "name")
    var name: String? = null,
    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = LocalDate.now(),
    @ManyToMany(mappedBy = "authors")
    var books: MutableSet<BookEntity> = mutableSetOf(),
) : BaseEntity() {
    fun addBook(book: BookEntity) {
        books.add(book)
        book.authors.add(this)
    }

    fun removeBook(book: BookEntity) {
        books.remove(book)
        book.authors.remove(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthorEntity) return false

        if (name != other.name) return false
        if (dateOfBirth != other.dateOfBirth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (dateOfBirth?.hashCode() ?: 0)
        return result
    }
}
