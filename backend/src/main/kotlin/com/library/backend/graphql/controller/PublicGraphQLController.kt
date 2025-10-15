package com.library.backend.graphql.controller

import com.library.backend.dto.book.BookFilter
import com.library.backend.dto.book.BookGenre
import com.library.backend.graphql.model.author.AuthorPage
import com.library.backend.graphql.model.book.BookPage
import com.library.backend.graphql.model.pagination.PageInfo
import com.library.backend.graphql.model.pagination.SortInput
import com.library.backend.mapper.AuthorMapper.toAuthorWithPublishedBooksDTO
import com.library.backend.mapper.BookMapper.toDTO
import com.library.backend.service.author.AuthorService
import com.library.backend.service.book.BookService
import com.library.backend.utils.SortUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class PublicGraphQLController(
    private val authorService: AuthorService,
    private val bookService: BookService,
) {
    @QueryMapping
    fun getAuthors(
        @Argument pageNumber: Int? = 0,
        @Argument pageSize: Int? = 10,
        @Argument sort: List<SortInput>?,
    ): AuthorPage {
        val defaultSort = Sort.by(Sort.Direction.DESC, "createdAt")
        val springSort = SortUtils.toSpringSort(sort, defaultSort)

        val pageable = PageRequest.of(pageNumber!!, pageSize!!, springSort)
        val pageResult = authorService.getAuthors(pageable).map { it.toAuthorWithPublishedBooksDTO() }

        return AuthorPage(
            pageInfo =
                PageInfo(
                    pageNumber = pageResult.number,
                    pageSize = pageResult.size,
                    totalPages = pageResult.totalPages,
                    totalElements = pageResult.totalElements,
                ),
            content = pageResult.content,
        )
    }

    @QueryMapping
    fun searchBooks(
        @Argument title: String?,
        @Argument isbn: String?,
        @Argument genre: BookGenre?,
        @Argument author: String?,
        @Argument pageNumber: Int? = 0,
        @Argument pageSize: Int? = 10,
        @Argument sort: List<SortInput>?,
    ): BookPage {
        val defaultSort = Sort.by(Sort.Direction.DESC, "publishingDateTime")
        val springSort = SortUtils.toSpringSort(sort, defaultSort)

        val pageable = PageRequest.of(pageNumber!!, pageSize!!, springSort)
        val filter = BookFilter(title, isbn, genre, author)
        val pageResult = bookService.searchBooks(filter, pageable).map { it.toDTO() }

        return BookPage(
            pageInfo =
                PageInfo(
                    pageNumber = pageResult.number,
                    pageSize = pageResult.size,
                    totalPages = pageResult.totalPages,
                    totalElements = pageResult.totalElements,
                ),
            content = pageResult.content,
        )
    }
}
