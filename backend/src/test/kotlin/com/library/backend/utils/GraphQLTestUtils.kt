package com.library.backend.utils

import com.library.backend.dto.book.BookGenre
import java.time.LocalDateTime

object GraphQLTestUtils {
    fun createAuthorMutation(
        name: String,
        dateOfBirth: String,
    ): String =
        """
        mutation {
            createAuthor(authorCreate: { name: "$name", dateOfBirth: "$dateOfBirth" }) {
                id
                name
                dateOfBirth
            }
        }
        """.trimIndent()

    fun updateAuthorMutation(
        id: Long,
        name: String? = null,
        dateOfBirth: String? = null,
    ): String =
        """
        mutation {
            updateAuthor(id: $id, authorUpdate: { ${createGraphQLObject("name" to name, "dateOfBirth" to dateOfBirth)} }) {
                id
                name
                dateOfBirth
            }
        }
        """.trimIndent()

    fun createBookMutation(
        title: String,
        isbn: String,
        genre: BookGenre,
        creationDateTime: String,
        publishingDateTime: String,
        authorIds: List<Long>,
    ): String =
        """
        mutation {
            publishBook(bookCreate: { title: "$title", isbn: "$isbn", genre: $genre, creationDateTime: "$creationDateTime", publishingDateTime: "$publishingDateTime", authorIds: $authorIds }) {
                id
                title
                isbn
                genre
                creationDateTime
                publishingDateTime
                authors {
                    id
                    name
                    dateOfBirth
                }
            }
        }
        """.trimIndent()

    private fun createGraphQLObject(vararg fields: Pair<String, Any?>): String =
        fields
            .filter { it.second != null }
            .joinToString(", ") { (k, v) -> """$k: "$v"""" }
}
