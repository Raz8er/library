package com.library.backend.graphql.config

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
import java.time.format.DateTimeFormatter
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
    fun runtimeWiringConfigurer(localDateScalar: GraphQLScalarType) =
        RuntimeWiringConfigurer { builder ->
            builder.scalar(localDateScalar)
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
        ): LocalDate = LocalDate.parse(input.toString(), dateFormatter)

        override fun parseLiteral(
            input: Value<*>,
            variables: CoercedVariables,
            graphQLContext: GraphQLContext,
            locale: Locale,
        ): LocalDate {
            if (input is StringValue) {
                return LocalDate.parse(input.value, dateFormatter)
            }
            throw CoercingParseLiteralException(
                "Expected AST type 'StringValue' but was '${input::class.simpleName}'.",
            )
        }
    }
}
