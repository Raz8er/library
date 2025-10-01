package com.library.backend.dto.cursor

data class CursorPageRequest(
    val cursor: String? = null,
    val size: Int? = 10,
    val sortBy: String? = null,
)
