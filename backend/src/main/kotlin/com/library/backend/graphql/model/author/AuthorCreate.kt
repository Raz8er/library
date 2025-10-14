package com.library.backend.graphql.model.author

import java.time.LocalDate

data class AuthorCreate(
    val name: String,
    val dateOfBirth: LocalDate,
)
