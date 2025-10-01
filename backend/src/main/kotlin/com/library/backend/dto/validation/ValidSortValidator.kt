package com.library.backend.dto.validation

import com.library.backend.dto.author.AuthorSort
import com.library.backend.dto.book.BookSort
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidSortValidator : ConstraintValidator<ValidSort, String> {
    private lateinit var type: ValidSort.SortType

    override fun initialize(constraintAnnotation: ValidSort) {
        type = constraintAnnotation.type
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        value ?: return true
        if (value.isBlank()) {
            return false
        }
        return when (type) {
            ValidSort.SortType.AUTHOR -> AuthorSort.isValidSortByValue(value)
            ValidSort.SortType.BOOK -> BookSort.isValidSortByValue(value)
        }
    }
}
