package com.library.backend.utils

import com.library.backend.dto.book.BookGenre
import java.security.SecureRandom

object BookGenreGenerator {
    fun generateBookGenre(): BookGenre {
        val bookGenreIndex = SecureRandom().nextInt(BookGenre.entries.size)
        return BookGenre.entries[bookGenreIndex]
    }
}
