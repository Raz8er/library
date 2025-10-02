package com.library.backend.api

import com.library.backend.dto.author.AuthorWithPublishedBooksDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.dto.book.BookFilter
import com.library.backend.dto.book.BookGenre
import com.library.backend.dto.validation.ValidBookGenre
import com.library.backend.mapper.AuthorMapper.toAuthorWithPublishedBooksDTO
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.author.AuthorService
import com.library.backend.service.book.BookService
import com.library.backend.utils.ResponseEntityUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/public")
class PublicController(
    private val authorService: AuthorService,
    private val bookService: BookService,
) {
    @GetMapping("/authors")
    fun getAuthors(
        @PageableDefault(sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<Page<AuthorWithPublishedBooksDTO>> {
        val body = authorService.getAuthors(pageable).map { it.toAuthorWithPublishedBooksDTO() }
        return ResponseEntityUtils.createResponse(body, HttpStatus.OK)
    }

    @GetMapping("/books")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) isbn: String?,
        @RequestParam(required = false) @ValidBookGenre genre: String?,
        @RequestParam(required = false) author: String?,
        @PageableDefault(sort = ["publishingDateTime"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<Page<BookDTO>> {
        val filter = BookFilter(title, isbn, BookGenre.getEnumValue(genre), author)
        val body = bookService.searchBooks(filter, pageable).map { it.toDTO() }
        return ResponseEntityUtils.createResponse(body, HttpStatus.OK)
    }
}
