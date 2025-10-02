package com.library.backend.api

import com.library.backend.dto.author.AuthorWithPublishedBooksDTO
import com.library.backend.dto.book.BookDTO
import com.library.backend.dto.book.BookFilter
import com.library.backend.dto.book.BookGenre
import com.library.backend.dto.cursor.CursorPageRequest
import com.library.backend.dto.cursor.CursorPageResponse
import com.library.backend.dto.validation.ValidBookGenre
import com.library.backend.dto.validation.ValidSort
import com.library.backend.mapper.AuthorMapper.toAuthorWithPublishedBooksDTO
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.author.AuthorService
import com.library.backend.service.book.BookService
import com.library.backend.utils.ResponseEntityUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/public")
class PublicControllerV2(
    private val authorService: AuthorService,
    private val bookService: BookService,
) {
    @GetMapping("/authors")
    fun getAuthors(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") size: Int?,
        @RequestParam(defaultValue = "createdAt") @ValidSort(type = ValidSort.SortType.AUTHOR) sortBy: String?,
    ): ResponseEntity<CursorPageResponse<AuthorWithPublishedBooksDTO>> {
        val cursorResponse = authorService.getAuthorsByCursor(CursorPageRequest(cursor, size, sortBy))
        val body =
            CursorPageResponse(
                cursorResponse.content.map { it.toAuthorWithPublishedBooksDTO() },
                cursorResponse.nextCursor,
            )
        return ResponseEntityUtils.createResponse(body, HttpStatus.OK)
    }

    @GetMapping("/books")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) isbn: String?,
        @RequestParam(required = false) @ValidBookGenre genre: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") size: Int?,
        @RequestParam(defaultValue = "publishingDateTime") @ValidSort(type = ValidSort.SortType.BOOK) sortBy: String?,
    ): ResponseEntity<CursorPageResponse<BookDTO>> {
        val filter = BookFilter(title, isbn, BookGenre.getEnumValue(genre), author)
        val cursorResponse = bookService.searchBooksByCursor(filter, CursorPageRequest(cursor, size, sortBy))
        val body =
            CursorPageResponse(
                content = cursorResponse.content.map { it.toDTO() },
                nextCursor = cursorResponse.nextCursor,
            )
        return ResponseEntityUtils.createResponse(body, HttpStatus.OK)
    }
}
