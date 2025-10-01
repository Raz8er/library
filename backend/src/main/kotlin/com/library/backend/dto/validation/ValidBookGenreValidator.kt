package com.library.backend.dto.validation

import com.library.backend.dto.book.BookGenre
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidBookGenreValidator : ConstraintValidator<ValidBookGenre, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        value ?: return true
        return value.isNotBlank() && BookGenre.isValidBookGenre(value)
    }
}
