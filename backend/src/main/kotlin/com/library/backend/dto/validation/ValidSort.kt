package com.library.backend.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Constraint(validatedBy = [ValidSortValidator::class])
annotation class ValidSort(
    val type: SortType,
    val message: String = "Invalid sortBy value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
) {
    enum class SortType {
        AUTHOR,
        BOOK,
    }
}
