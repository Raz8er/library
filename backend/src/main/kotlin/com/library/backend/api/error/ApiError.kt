package com.library.backend.api.error

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.ConstraintViolation
import org.hibernate.validator.internal.engine.path.PathImpl
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import java.time.LocalDateTime

data class ApiError(
    var status: HttpStatus? = null,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    var timestamp: LocalDateTime = LocalDateTime.now(),
    var message: String? = null,
    var debugMessage: String? = null,
    var subErrors: MutableList<ApiSubError>? = null,
) {
    constructor(status: HttpStatus, ex: Throwable) : this(
        status = status,
        message = "Unexpected error",
        debugMessage = ex.localizedMessage,
    )

    constructor(status: HttpStatus, message: String, ex: Throwable) : this(
        status = status,
        message = message,
        debugMessage = ex.localizedMessage,
    )

    private fun addSubError(subError: ApiSubError) {
        if (subErrors == null) {
            subErrors = mutableListOf()
        }
        subErrors!!.add(subError)
    }

    fun addValidationError(
        obj: String? = null,
        field: String? = null,
        rejectedValue: Any? = null,
        message: String? = null,
    ) {
        addSubError(ApiValidationError(obj, field, rejectedValue, message))
    }

    private fun addValidationError(
        obj: String,
        message: String?,
    ) {
        addSubError(ApiValidationError(obj, message))
    }

    private fun addValidationError(fieldError: FieldError) {
        addValidationError(
            fieldError.objectName,
            fieldError.field,
            fieldError.rejectedValue,
            fieldError.defaultMessage,
        )
    }

    fun addFieldValidationErrors(fieldErrors: List<FieldError>) {
        fieldErrors.forEach { addValidationError(it) }
    }

    private fun addValidationError(objectError: ObjectError) {
        addValidationError(objectError.objectName, objectError.defaultMessage)
    }

    fun addGlobalValidationErrors(globalErrors: List<ObjectError>) {
        globalErrors.forEach { addValidationError(it) }
    }

    private fun addValidationError(cv: ConstraintViolation<*>) {
        addValidationError(
            cv.rootBeanClass.simpleName,
            (cv.propertyPath as PathImpl).leafNode.asString(),
            cv.invalidValue,
            cv.message,
        )
    }

    fun addConstraintValidationErrors(constraintViolations: Set<ConstraintViolation<*>>) {
        constraintViolations.forEach { addValidationError(it) }
    }
}
