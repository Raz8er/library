package com.library.backend.utils

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

    private fun createGraphQLObject(vararg fields: Pair<String, Any?>): String =
        fields
            .filter { it.second != null }
            .joinToString(", ") { (k, v) -> """$k: "$v"""" }
}
