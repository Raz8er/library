package com.library.backend.entity

import com.library.backend.dto.book.BookGenre
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.NaturalId
import java.time.LocalDateTime

@Entity
@Table(
    name = "books",
    uniqueConstraints = [UniqueConstraint(name = "books_isbn_uq", columnNames = arrayOf("isbn"))],
    indexes = [
        Index(name = "books_title_idx", columnList = "title"),
        Index(name = "books_genre_idx", columnList = "genre"),
        Index(name = "books_publishing_date_time_idx", columnList = "publishing_date_time"),
    ],
)
class BookEntity(
    @Column(name = "title")
    var title: String? = null,
    @NaturalId
    @Column(name = "isbn", unique = true)
    var isbn: String? = null,
    @Column(name = "genre")
    var genre: BookGenre? = null,
    @Column(name = "creation_date_time")
    var creationDateTime: LocalDateTime? = LocalDateTime.now(),
    @Column(name = "publishing_date_time")
    var publishingDateTime: LocalDateTime? = LocalDateTime.now(),
    @ManyToMany(
        cascade = [
            CascadeType.PERSIST,
            CascadeType.MERGE,
        ],
    )
    @JoinTable(
        name = "books_authors",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")],
    )
    var authors: MutableSet<AuthorEntity> = mutableSetOf(),
) : BaseEntity() {
    fun addAuthor(author: AuthorEntity) {
        this.authors.add(author)
        author.books.add(this)
    }

    fun removeAuthor(author: AuthorEntity) {
        this.authors.remove(author)
        author.books.remove(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookEntity) return false

        if (title != other.title) return false
        if (isbn != other.isbn) return false
        if (genre != other.genre) return false
        if (creationDateTime != other.creationDateTime) return false
        if (publishingDateTime != other.publishingDateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (isbn?.hashCode() ?: 0)
        result = 31 * result + (genre?.hashCode() ?: 0)
        result = 31 * result + (creationDateTime?.hashCode() ?: 0)
        result = 31 * result + (publishingDateTime?.hashCode() ?: 0)
        return result
    }
}
