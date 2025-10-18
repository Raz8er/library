package com.library.backend.mapper

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.BookEntity
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.mapper.AuthorMapper.toEntity

object BookMapper {
    fun BookEntity.toDTO(): BookDTO =
        BookDTO(
            id = this.id,
            title = this.title,
            isbn = this.isbn,
            genre = this.genre,
            creationDateTime = this.creationDateTime,
            publishingDateTime = this.publishingDateTime,
            authors = this.authors.map { it.toDTO() }.toMutableSet(),
        )

    fun BookDTO.toEntity(): BookEntity {
        val entity =
            BookEntity(
                title = this.title,
                isbn = this.isbn,
                genre = this.genre,
                creationDateTime = this.creationDateTime,
                publishingDateTime = this.publishingDateTime,
            )
        this.authors.forEach { entity.addAuthor(it.toEntity()) }
        return entity
    }

    fun BookCreateDTO.toEntity(): BookEntity =
        BookEntity(
            title = this.title,
            isbn = this.isbn,
            genre = BookGenre.getEnumValue(this.genre),
            creationDateTime = this.creationDateTime,
        )
}
