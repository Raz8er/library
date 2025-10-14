package com.library.backend.graphql.controller

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.graphql.model.book.BookCreate
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.book.BookService
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
        @Argument bookCreate: BookCreate,
    ): BookDTO {
        val dto =
            BookCreateDTO(
                title = bookCreate.title,
                isbn = bookCreate.isbn,
                genre = bookCreate.genre,
                creationDateTime = bookCreate.creationDateTime,
                authorIds = bookCreate.authorIds,
            )
        return bookService.createBook(dto).toDTO()
    }
}
