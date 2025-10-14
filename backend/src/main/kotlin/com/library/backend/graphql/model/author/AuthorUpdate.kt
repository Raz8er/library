package com.library.backend.graphql.model.author

import java.time.LocalDate

data class AuthorUpdate(
    val name: String?,
    val dateOfBirth: LocalDate?,
)
