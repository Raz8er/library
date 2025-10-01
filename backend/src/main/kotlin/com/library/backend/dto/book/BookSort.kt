package com.library.backend.dto.book

enum class BookSort(
    val value: String,
) {
    PUBLISHING_DATE_TIME("publishingDateTime"),
    ;

    companion object {
        fun isValidSortByValue(value: String): Boolean = BookSort.entries.any { it.value.equals(value, ignoreCase = true) }

        fun getEnumValue(value: String): BookSort = BookSort.entries.first { it.value.equals(value, ignoreCase = true) }
    }
}
