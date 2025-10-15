package com.library.backend.utils

import com.library.backend.graphql.exception.GraphQLValidationException
import jakarta.validation.Validation

object GraphQLValidationUtils {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    fun <T> validate(obj: T) {
        val violations = validator.validate(obj)
        if (violations.isEmpty()) {
            return
        }
        val message = violations.joinToString("; ") { "${it.propertyPath}: ${it.message}" }
        throw GraphQLValidationException(message)
    }
}
