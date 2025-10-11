package com.library.backend.dto

import com.library.backend.dto.author.AuthorCreateDTO
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.assertTrue

class AuthorCreateDTOValidationTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    companion object {
        @JvmStatic
        fun getInvalidAuthors(): Stream<Arguments> =
            Stream.of(
                Arguments.of(AuthorCreateDTO(name = null, dateOfBirth = LocalDate.now()), "name"),
                Arguments.of(AuthorCreateDTO(name = " ", dateOfBirth = LocalDate.now()), "name"),
                Arguments.of(AuthorCreateDTO(name = "", dateOfBirth = LocalDate.now()), "name"),
                Arguments.of(AuthorCreateDTO(name = "John", dateOfBirth = null as LocalDate?), "dateOfBirth"),
            )
    }

    @ParameterizedTest
    @MethodSource("getInvalidAuthors")
    fun `should fail validation for invalid fields`(
        dto: AuthorCreateDTO,
        expectedField: String,
    ) {
        val violations = validator.validate(dto)
        assertTrue(
            violations.any { it.propertyPath.toString() == expectedField },
            "Expected validation error on field '$expectedField', but got $violations",
        )
    }
}
