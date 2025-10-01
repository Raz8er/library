package com.library.backend.dto.author

enum class AuthorSort(
    val value: String,
) {
    CREATED_AT("createdAt"),
    BOOKS("numberOfPublishedBooks"),
    ;

    companion object {
        fun isValidSortByValue(value: String): Boolean = AuthorSort.entries.any { it.value.equals(value, ignoreCase = true) }

        fun getEnumValue(value: String): AuthorSort = entries.first { it.value.equals(value, ignoreCase = true) }
    }
}
