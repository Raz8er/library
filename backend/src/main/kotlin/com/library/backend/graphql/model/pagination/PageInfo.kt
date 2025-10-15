package com.library.backend.graphql.model.pagination

data class PageInfo(
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
)
