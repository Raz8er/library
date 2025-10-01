package com.library.backend.dto.book

enum class BookGenre(
    val text: String,
) {
    ROMANCE("Romance"),
    THRILLER("Thriller"),
    COMEDY("Comedy"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    SCI_FI("Sci-Fi"),
    ;

    companion object {
        fun getEnumValue(text: String?): BookGenre? = entries.find { it.text.equals(text, ignoreCase = true) }

        fun isValidBookGenre(value: String): Boolean = BookGenre.entries.any { it.text.equals(value, ignoreCase = true) }
    }
}
