package com.library.backend.api

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.book.BookService
import com.library.backend.utils.ResponseEntityUtils
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/books")
class BookController(
    private val bookService: BookService,
) {
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_author') or hasRole('ADMIN')")
    fun publishBook(
        @Valid @RequestBody dto: BookCreateDTO,
    ): ResponseEntity<BookDTO> = ResponseEntityUtils.createResponse(bookService.createBook(dto).toDTO(), HttpStatus.CREATED)
}
