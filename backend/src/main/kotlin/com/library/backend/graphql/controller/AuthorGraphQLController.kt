package com.library.backend.graphql.controller

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.graphql.model.author.AuthorCreate
import com.library.backend.graphql.model.author.AuthorUpdate
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.service.author.AuthorService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class AuthorGraphQLController(
    private val authorService: AuthorService,
) {
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    fun createAuthor(
        @Argument authorCreate: @Valid AuthorCreate,
    ): AuthorDTO {
        val dto =
            AuthorCreateDTO(
                name = authorCreate.name,
                dateOfBirth = authorCreate.dateOfBirth,
            )
        return authorService.createAuthor(dto).toDTO()
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    fun updateAuthor(
        @Argument id: Long,
        @Argument authorUpdate: @Valid AuthorUpdate,
    ): AuthorDTO {
        val dto =
            AuthorUpdateDTO(
                name = authorUpdate.name,
                dateOfBirth = authorUpdate.dateOfBirth,
            )
        return authorService.updateAuthor(id, dto).toDTO()
    }
}
