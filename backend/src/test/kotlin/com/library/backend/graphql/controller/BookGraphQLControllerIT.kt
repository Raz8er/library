package com.library.backend.graphql.controller

import com.library.backend.creator.TestCreator
import com.library.backend.dto.author.AuthorDTO
import com.library.backend.mapper.AuthorMapper.toDTO
import com.library.backend.testbase.GraphQLTestBase
import com.library.backend.utils.BookGenreGenerator
import com.library.backend.utils.BookISBNGenerator
import com.library.backend.utils.GraphQLTestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

class BookGraphQLControllerIT : GraphQLTestBase() {
    @Autowired
    private lateinit var creator: TestCreator

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should publish a book via GQL interface`() {
        val author1 = creator.author().create(name = "Author1")
        val author2 = creator.author().create(name = "Author2")
        val mutation =
            GraphQLTestUtils.createBookMutation(
                title = "Book1",
                isbn = BookISBNGenerator.generateISBN(),
                genre = BookGenreGenerator.generateBookGenre(),
                creationDateTime = "28-09-1975 12:43:51",
                publishingDateTime = "28-09-1985 12:43:51",
                authorIds = listOf(author1.id!!, author2.id!!),
            )
        graphQlTester
            .document(mutation)
            .execute()
            .path("publishBook.title")
            .entity(String::class.java)
            .isEqualTo("Book1")
            .path("publishBook.authors")
            .entityList(AuthorDTO::class.java)
            .contains(author1.toDTO(), author2.toDTO())
    }
}
