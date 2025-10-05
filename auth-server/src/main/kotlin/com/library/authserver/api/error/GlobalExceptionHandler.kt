package com.library.authserver.api.error

import com.library.authserver.exception.ClientAlreadyExistsException
import com.library.authserver.exception.InvalidClientCredentialsException
import com.library.authserver.exception.InvalidGrantTypeException
import com.library.authserver.utils.ResponseEntityUtils
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

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
        apiError.debugMessage = ex.localizedMessage
        val servletWebRequest = request as ServletWebRequest
        log.info("${servletWebRequest.httpMethod} to ${servletWebRequest.request.servletPath}")
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
        val apiError =
            ApiError(
                status = status,
                message = "Validation error",
            )
        ex.parameterValidationResults.forEach {
            apiError.addValidationError(
                message = it.resolvableErrors.first().defaultMessage,
                field = it.methodParameter.parameterName,
                rejectedValue = it.argument,
            )
        }
        return ResponseEntityUtils.createResponse(apiError, status)
    }

    @ExceptionHandler(InvalidClientCredentialsException::class)
    fun handleInvalidClientCredentialsException(ex: InvalidClientCredentialsException): ResponseEntity<Any?> {
        val apiError = ApiError(status = ex.status, message = ex.message)
        return ResponseEntityUtils.createResponse(apiError, ex.status)
    }

    @ExceptionHandler(ClientAlreadyExistsException::class)
    fun handleClientAlreadyExistsException(ex: ClientAlreadyExistsException): ResponseEntity<Any?> {
        val apiError = ApiError(status = ex.status, message = ex.message)
        return ResponseEntityUtils.createResponse(apiError, ex.status)
    }

    @ExceptionHandler(InvalidGrantTypeException::class)
    fun handleInvalidGrantTypeException(ex: InvalidGrantTypeException): ResponseEntity<Any?> {
        val apiError = ApiError(status = ex.status, message = ex.message)
        return ResponseEntityUtils.createResponse(apiError, ex.status)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Any?> {
        val status = HttpStatus.BAD_REQUEST
        val apiError = ApiError(status)
        apiError.message = "Validation error"
        apiError.addConstraintValidationErrors(ex.constraintViolations)
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
