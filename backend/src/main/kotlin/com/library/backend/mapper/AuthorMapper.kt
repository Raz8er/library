package com.library.backend.mapper

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.dto.author.AuthorWithPublishedBooksDTO
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection

object AuthorMapper {
    fun AuthorEntity.toDTO(): AuthorDTO =
        AuthorDTO(
            id = this.id,
            name = this.name,
            dateOfBirth = this.dateOfBirth,
        )

    fun AuthorWithPublishedBooksProjection.toAuthorWithPublishedBooksDTO(): AuthorWithPublishedBooksDTO =
        AuthorWithPublishedBooksDTO(
            id = this.id,
            name = this.name,
            dateOfBirth = this.dateOfBirth,
            createdAt = this.createdAt.toLocalDateTime(),
            numberOfPublishedBooks = this.numberOfPublishedBooks.toInt(),
        )

    fun AuthorDTO.toEntity(): AuthorEntity =
        AuthorEntity(
            name = this.name,
            dateOfBirth = this.dateOfBirth,
        )

    fun AuthorCreateDTO.toEntity(): AuthorEntity =
        AuthorEntity(
            name = this.name,
            dateOfBirth = this.dateOfBirth,
        )

    fun AuthorUpdateDTO.toEntity(): AuthorEntity =
        AuthorEntity(
            name = this.name,
            dateOfBirth = this.dateOfBirth,
        )

    fun AuthorEntity.updateEntity(updatedEntity: AuthorEntity): AuthorEntity {
        if (updatedEntity.name != null) {
            this.name = updatedEntity.name
        }
        if (updatedEntity.dateOfBirth != null) {
            this.dateOfBirth = updatedEntity.dateOfBirth
        }
        return this
    }
}
