package com.library.authserver.dto.token

enum class TokenScope(
    val value: String,
    val user: String,
) {
    ADMIN("admin", "Admin"),
    AUTHOR("author", "Author"),
    ;

    companion object {
        fun isValidScope(scope: String): Boolean = TokenScope.entries.any { it.value == scope.lowercase() }

        fun getEnumValueFrom(scope: String?): TokenScope? =
            scope?.let { scope ->
                TokenScope.entries.find { it.value == scope.lowercase() }
            }
    }
}
