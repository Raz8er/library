package com.library.backend.converter

import com.library.backend.dto.book.BookGenre
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class BookGenreConverter : AttributeConverter<BookGenre, String> {
    override fun convertToDatabaseColumn(bookGenre: BookGenre?): String? {
        if (bookGenre == null) {
            return null
        }
        return bookGenre.text
    }

    override fun convertToEntityAttribute(bookGenreText: String?): BookGenre? {
        if (bookGenreText == null) {
            return null
        }
        return BookGenre.getEnumValue(bookGenreText)
    }
}
