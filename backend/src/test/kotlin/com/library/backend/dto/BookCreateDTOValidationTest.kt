package com.library.backend.dto

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.utils.BookGenreGenerator
import com.library.backend.utils.BookISBNGenerator
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertTrue

class BookCreateDTOValidationTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    companion object {
        @JvmStatic
        fun getInvalidBooks(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    BookCreateDTO(
                        title = null,
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "title",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "title",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = " ",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "title",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = null,
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "isbn",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = "",
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "isbn",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = " ",
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "isbn",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = "123456789",
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "isbn",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = null,
                        creationDateTime = LocalDateTime.now(),
                    ),
                    "genre",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = null,
                    ),
                    "creationDateTime",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                        authorIds = null,
                    ),
                    "authorIds",
                ),
                Arguments.of(
                    BookCreateDTO(
                        title = "The Lord of the Rings",
                        isbn = BookISBNGenerator.generateISBN(),
                        genre = BookGenreGenerator.generateBookGenre().name,
                        creationDateTime = LocalDateTime.now(),
                        authorIds = mutableSetOf(),
                    ),
                    "authorIds",
                ),
            )
    }

    @ParameterizedTest
    @MethodSource("getInvalidBooks")
    fun `should fail validation for invalid fields`(
        dto: BookCreateDTO,
        expectedField: String,
    ) {
        val violations = validator.validate(dto)
        assertTrue(
            violations.any { it.propertyPath.toString() == expectedField },
            "Expected validation error on field '$expectedField', but got $violations",
        )
    }
}
