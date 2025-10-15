package com.library.backend.graphql.controller

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.service.author.AuthorService
import com.library.backend.utils.GraphQLValidationUtils
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
        @Argument authorCreate: AuthorCreateDTO,
    ): AuthorDTO {
        GraphQLValidationUtils.validate(authorCreate)
        return authorService.createAuthor(authorCreate).toDTO()
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_admin') or hasRole('ADMIN')")
    fun updateAuthor(
        @Argument id: Long,
        @Argument authorUpdate: AuthorUpdateDTO,
    ): AuthorDTO {
        GraphQLValidationUtils.validate(authorUpdate)
        return authorService.updateAuthor(id, authorUpdate).toDTO()
    }
}
