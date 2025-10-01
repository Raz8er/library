package com.library.backend.dto.book

data class BookFilter(
    val title: String? = null,
    val isbn: String? = null,
    val genre: BookGenre? = null,
    val author: String? = null,
)
