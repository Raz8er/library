package com.library.backend.graphql.model.pagination

data class SortInput(
    val field: String,
    val direction: SortDirection? = null,
) {
    enum class SortDirection {
        ASC,
        DESC,
    }
}
