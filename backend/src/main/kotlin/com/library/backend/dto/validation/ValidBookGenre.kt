package com.library.backend.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Constraint(validatedBy = [ValidBookGenreValidator::class])
annotation class ValidBookGenre(
    val message: String = "Invalid book genre",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
