package com.library.authserver.dto.validation

import com.library.authserver.dto.token.TokenScope
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidScopeValidator : ConstraintValidator<ValidScope, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        value ?: return true
        return value.isNotBlank() && TokenScope.isValidScope(value)
    }
}
