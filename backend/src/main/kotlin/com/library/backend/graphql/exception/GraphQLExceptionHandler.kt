package com.library.backend.graphql.exception

import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun resolveToSingleError(
        ex: Throwable,
        env: DataFetchingEnvironment,
    ): GraphQLError? =
        when (ex) {
            is EntityNotFoundException -> toGraphQLError(ex, ex.message!!)
            is ConstraintViolationException -> handleConstraintViolationException(ex)
            is MethodArgumentNotValidException -> handleMethodArgumentNotValidException(ex)
            is GraphQLValidationException -> handleValidationException(ex)
            is Exception -> toGraphQLError(ex, ex.message!!)
            else -> super.resolveToSingleError(ex, env)
        }

    private fun toGraphQLError(
        ex: Throwable,
        message: String,
        errorType: ErrorType = ErrorType.DataFetchingException,
    ): GraphQLError {
        log.error("GQL exception", ex)
        return GraphqlErrorBuilder
            .newError()
            .message(message)
            .errorType(errorType)
            .build()
    }

    private fun handleConstraintViolationException(ex: ConstraintViolationException): GraphQLError? {
        val errorMessages = mutableSetOf<String>()
        ex.constraintViolations.forEach {
            errorMessages.add(
                "Field '${it.propertyPath}' ${it.message}, but value was [${it.invalidValue}]",
            )
        }
        val message = errorMessages.joinToString("\n")
        log.error("GQL constraint violation exception", ex)
        return toGraphQLError(ex, message)
    }

    private fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): GraphQLError {
        log.error("GQL method argument not valid exception", ex)
        val message = "Validation failed: ${ex.bindingResult.allErrors.joinToString { it.defaultMessage ?: "" }}"
        return toGraphQLError(ex, message)
    }

    private fun handleValidationException(ex: GraphQLValidationException): GraphQLError {
        log.error("GQL validation exception", ex)
        return toGraphQLError(ex, ex.message, ErrorType.ValidationError)
    }
}
