package com.library.backend.dto.cursor

data class CursorPageResponse<T>(
    val content: List<T> = emptyList(),
    val nextCursor: String?,
)
