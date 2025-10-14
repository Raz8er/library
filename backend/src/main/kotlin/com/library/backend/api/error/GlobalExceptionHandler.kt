package com.library.backend.api.error

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.library.backend.utils.DateTimeUtils
import com.library.backend.utils.ResponseEntityUtils
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.LocalDate
import java.time.LocalDateTime

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val error = "${ex.parameterName} is missing"
        return ResponseEntityUtils.createResponse(ApiError(HttpStatus.BAD_REQUEST, error, ex), status)
    }

    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val builder = StringBuilder()
        builder.append(ex.contentType)
        builder.append(" media type is not supported. Supported media types are ")
        ex.supportedMediaTypes.forEach { builder.append(it).append(", ") }
        return ResponseEntityUtils.createResponse(
            ApiError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                builder.substring(0, builder.length - 2),
                ex,
            ),
            status,
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val apiError = ApiError(HttpStatus.BAD_REQUEST)
        apiError.message = "Validation error"
        apiError.addFieldValidationErrors(ex.bindingResult.fieldErrors)
        apiError.addGlobalValidationErrors(ex.bindingResult.globalErrors)
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val apiError = ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex)
        val cause = ex.cause
        if (cause is InvalidFormatException) {
            val fieldName = cause.path.joinToString(".") { it.fieldName }

            when (cause.targetType) {
                LocalDate::class.java -> {
                    apiError.addValidationError(
                        field = fieldName,
                        rejectedValue = cause.value,
                        message = "Field $fieldName has invalid date format. Expected format: ${DateTimeUtils.DATE_FORMAT}",
                    )
                }

                LocalDateTime::class.java -> {
                    apiError.addValidationError(
                        field = fieldName,
                        rejectedValue = cause.value,
                        message = "Field $fieldName has invalid datetime format. Expected format: ${DateTimeUtils.DATE_TIME_FORMAT}",
                    )
                }
            }
        } else {
            apiError.debugMessage = ex.localizedMessage
        }
        val servletWebRequest = request as ServletWebRequest
        log.info("{} to {}", servletWebRequest.httpMethod, servletWebRequest.request.servletPath)
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    override fun handleHttpMessageNotWritable(
        ex: HttpMessageNotWritableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val error = "Error writing JSON output"
        return ResponseEntityUtils.createResponse(ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex), status)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val apiError = ApiError(HttpStatus.BAD_REQUEST)
        apiError.message = "Could not find the ${ex.httpMethod} method for URL ${ex.requestURL}"
        apiError.debugMessage = ex.message
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val status = HttpStatus.BAD_REQUEST
        val debugMessage =
            ex.parameterValidationResults.flatMap { pvr -> pvr.resolvableErrors.map { it.defaultMessage } }
        val apiError =
            ApiError(
                status = status,
                message = "Validation error",
                debugMessage = debugMessage.joinToString("; "),
            )
        ex.parameterValidationResults.forEach {
            apiError.addValidationError(
                field = it.methodParameter.parameterName,
                rejectedValue = it.argument,
            )
        }
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    override fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<in Any>? {
        val status = HttpStatus.NOT_FOUND
        val message = "Resource path ${ex.resourcePath} not found"
        val error = ApiError(status = status, message = message, debugMessage = ex.localizedMessage)
        return ResponseEntityUtils.createResponse(error, status)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Any?> {
        val status = HttpStatus.BAD_REQUEST
        val apiError = ApiError(status)
        apiError.message = "Validation error"
        apiError.debugMessage = ex.message
        apiError.addConstraintValidationErrors(ex.constraintViolations)
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Any?> {
        val status = HttpStatus.NOT_FOUND
        val apiError = ApiError(status)
        apiError.message = ex.message
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<Any?> {
        if (ex.cause is ConstraintViolationException) {
            val status = HttpStatus.CONFLICT
            return ResponseEntityUtils.createResponse(ApiError(status, "Database error", ex.cause!!), status)
        }
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntityUtils.createResponse(ApiError(status, ex), status)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<Any?> {
        val status = HttpStatus.BAD_REQUEST
        val apiError = ApiError(status)
        apiError.message =
            "The parameter '${ex.name}' of value '${ex.value}' could not be converted to type '${ex.requiredType?.simpleName}'"
        apiError.debugMessage = ex.message
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDenied(ex: AuthorizationDeniedException): ResponseEntity<Any?> {
        val status = HttpStatus.FORBIDDEN
        val apiError = ApiError(status = status, message = ex.localizedMessage)
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Any?> {
        val message = "Unexpected error occurred"
        log.error(message, ex)
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val apiError = ApiError(status = status, message = message, ex = ex)
        return ResponseEntityUtils.createResponse(apiError, status)
    }
}
