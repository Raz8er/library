package com.library.backend.graphql.controller

import com.library.backend.testbase.GraphQLTestBase
import com.library.backend.utils.GraphQLTestUtils
import org.junit.jupiter.api.Test
import org.springframework.security.test.context.support.WithMockUser

class AuthorGraphQLControllerIT : GraphQLTestBase() {
    @Test
    @WithMockUser(authorities = ["SCOPE_admin"])
    fun `should create author via GQL interface`() {
        val mutation = GraphQLTestUtils.createAuthorMutation("John Doe", "05-07-1982")
        graphQlTester
            .document(mutation)
            .execute()
            .path("createAuthor.name")
            .entity(String::class.java)
            .isEqualTo("John Doe")
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_admin"])
    fun `should update author via GQL interface`() {
        val createAuthorMutation = GraphQLTestUtils.createAuthorMutation("John Doe", "05-07-1982")
        val exisingAuthor =
            graphQlTester
                .document(createAuthorMutation)
                .execute()
        val authorId =
            exisingAuthor
                .path("createAuthor.id")
                .entity(Long::class.java)
                .get()

        val updateAuthorMutation = GraphQLTestUtils.updateAuthorMutation(authorId, name = "John Doe New")
        graphQlTester
            .document(updateAuthorMutation)
            .execute()
            .path("updateAuthor.name")
            .entity(String::class.java)
            .isEqualTo("John Doe New")
            .path("updateAuthor.dateOfBirth")
            .entity(String::class.java)
            .isEqualTo(exisingAuthor.path("createAuthor.dateOfBirth").entity(String::class.java).get())
    }
}
