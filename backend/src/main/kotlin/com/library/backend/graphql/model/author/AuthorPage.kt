package com.library.backend.graphql.model.author

import com.library.backend.dto.author.AuthorWithPublishedBooksDTO
import com.library.backend.graphql.model.pagination.PageInfo

data class AuthorPage(
    val pageInfo: PageInfo,
    val content: List<AuthorWithPublishedBooksDTO> = emptyList(),
)
