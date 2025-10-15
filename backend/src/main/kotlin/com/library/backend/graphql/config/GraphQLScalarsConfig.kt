package com.library.backend.graphql.config

import com.library.backend.graphql.exception.GraphQLValidationException
import com.library.backend.utils.DateTimeUtils
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

@Configuration
class GraphQLScalarsConfig {
    @Bean
    fun localDateScalar(): GraphQLScalarType =
        GraphQLScalarType
            .newScalar()
            .name("LocalDate")
            .description("LocalDate custom scalar with ${DateTimeUtils.DATE_FORMAT} format")
            .coercing(DateCoercing())
            .build()

    @Bean
    fun localDateTimeScalar(): GraphQLScalarType =
        GraphQLScalarType
            .newScalar()
            .name("LocalDateTime")
            .description("LocalDateTime custom scalar with ${DateTimeUtils.DATE_TIME_FORMAT} format")
            .coercing(DateTimeCoercing())
            .build()

    @Bean
    fun runtimeWiringConfigurer(
        localDateScalar: GraphQLScalarType,
        localDateTimeScalar: GraphQLScalarType,
    ) = RuntimeWiringConfigurer { builder ->
        builder.scalar(localDateScalar)
        builder.scalar(localDateTimeScalar)
    }

    class DateCoercing : Coercing<LocalDate, String> {
        private val dateFormatter = DateTimeFormatter.ofPattern(DateTimeUtils.DATE_FORMAT)

        override fun serialize(
            dataFetcherResult: Any,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): String = (dataFetcherResult as LocalDate).format(dateFormatter)

        override fun parseValue(
            input: Any,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): LocalDate =
            try {
                LocalDate.parse(input.toString(), dateFormatter)
            } catch (_: DateTimeParseException) {
                throw GraphQLValidationException("Invalid date format for value: $input, should be ${DateTimeUtils.DATE_FORMAT}")
            }

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): LocalDate {
            if (input is StringValue) {
                return try {
                    LocalDate.parse(input.value, dateFormatter)
                } catch (_: DateTimeParseException) {
                    throw GraphQLValidationException("Invalid date format for value: $input, should be ${DateTimeUtils.DATE_FORMAT}")
                }
            }
            throw CoercingParseLiteralException(
                "Expected AST type 'StringValue' but was '${input::class.simpleName}'.",
            )
        }
    }

    class DateTimeCoercing : Coercing<LocalDateTime, String> {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern(DateTimeUtils.DATE_TIME_FORMAT)

        override fun serialize(
            dataFetcherResult: Any,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): String = (dataFetcherResult as LocalDateTime).format(dateTimeFormatter)

        override fun parseValue(
            input: Any,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): LocalDateTime =
            try {
                LocalDateTime.parse(input.toString(), dateTimeFormatter)
            } catch (_: DateTimeParseException) {
                throw GraphQLValidationException("Invalid datetime format for value: $input, should be ${DateTimeUtils.DATE_TIME_FORMAT}")
            }

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): LocalDateTime {
            if (input is StringValue) {
                return try {
                    LocalDateTime.parse(input.value, dateTimeFormatter)
                } catch (_: DateTimeParseException) {
                    throw GraphQLValidationException(
                        "Invalid datetime format for value: $input, should be ${DateTimeUtils.DATE_TIME_FORMAT}",
                    )
                }
            }
            throw CoercingParseLiteralException(
                "Expected AST type 'StringValue' but was '${input::class.simpleName}'.",
            )
        }
    }
}
