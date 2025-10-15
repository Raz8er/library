package com.library.backend.graphql.model.book

import com.library.backend.dto.book.BookDTO
import com.library.backend.graphql.model.pagination.PageInfo

data class BookPage(
    val pageInfo: PageInfo,
    val content: List<BookDTO> = emptyList(),
)
