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
            is EntityNotFoundException -> toGraphQLError(ex)
            is ConstraintViolationException -> handleConstraintViolationException(ex)
            is MethodArgumentNotValidException -> handleMethodArgumentNotValidException(ex)
            is GraphQLValidationException -> handleValidationException(ex)
            is Exception -> toGraphQLError(ex)
            else -> super.resolveToSingleError(ex, env)
        }

    private fun toGraphQLError(ex: Throwable): GraphQLError {
        log.warn("Exception while handling request: ${ex.message}", ex)
        return GraphqlErrorBuilder
            .newError()
            .message(ex.message)
            .errorType(ErrorType.DataFetchingException)
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
        log.warn("Exception while handling request: $message", ex)
        return GraphqlErrorBuilder
            .newError()
            .message(message)
            .errorType(ErrorType.DataFetchingException)
            .build()
    }

    private fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): GraphQLError =
        GraphQLError
            .newError()
            .message("Validation failed: ${ex.bindingResult.allErrors.joinToString { it.defaultMessage ?: "" }}")
            .build()

    private fun handleValidationException(ex: GraphQLValidationException): GraphQLError =
        GraphQLError
            .newError()
            .errorType(ErrorType.ValidationError)
            .message(ex.message)
            .build()
}
