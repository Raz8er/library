package com.library.backend.graphql.exception

class GraphQLValidationException(
    override val message: String,
) : RuntimeException(message)
