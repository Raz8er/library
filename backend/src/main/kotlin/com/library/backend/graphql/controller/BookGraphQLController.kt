package com.library.backend.graphql.controller

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.book.BookService
import com.library.backend.utils.GraphQLValidationUtils
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class BookGraphQLController(
    private val bookService: BookService,
) {
    @MutationMapping
    @PreAuthorize("hasAuthority('SCOPE_author') or hasRole('ADMIN')")
    fun publishBook(
        @Argument bookCreate: BookCreateDTO,
    ): BookDTO {
        GraphQLValidationUtils.validate(bookCreate)
        return bookService.createBook(bookCreate).toDTO()
    }
}
