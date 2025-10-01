package com.library.backend.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    companion object {
        private const val BEARER_AUTH = "bearerAuth"
    }

    @Bean
    fun openApi(): OpenAPI = OpenAPI().info(Info().title("Library API").version("1.0").description("API for managing authors and books"))

    @Bean
    fun authorOpenApi(): GroupedOpenApi {
        val path = "/api/v1/authors/**"
        return GroupedOpenApi
            .builder()
            .group("authors")
            .pathsToMatch(path)
            .addOpenApiCustomizer(securedOpenApiCustomizer(authorApiInfo()))
            .build()
    }

    @Bean
    fun bookOpenApi(): GroupedOpenApi {
        val path = "/api/v1/books/**"
        return GroupedOpenApi
            .builder()
            .group("books")
            .pathsToMatch(path)
            .addOpenApiCustomizer(securedOpenApiCustomizer(bookApiInfo()))
            .build()
    }

    @Bean
    fun publicOpenApi(): GroupedOpenApi {
        val path = "/api/v1/public/**"
        return GroupedOpenApi
            .builder()
            .group("public")
            .pathsToMatch(path)
            .addOpenApiCustomizer(publicOpenApiCustomizer())
            .build()
    }

    private fun authorApiInfo(): Info =
        Info()
            .title("Author API")
            .description("API used by administrators for creating and updating authors.")
            .version("v1")

    private fun bookApiInfo(): Info =
        Info()
            .title("Book API")
            .description("API used by authors for publishing books.")
            .version("v1")

    private fun securedOpenApiCustomizer(info: Info): OpenApiCustomizer =
        OpenApiCustomizer { openApi: OpenAPI ->
            openApi.info(info)
            openApi.addSecurityItem(SecurityRequirement().addList(BEARER_AUTH))
            val components = openApi.components ?: Components()
            components.addSecuritySchemes(
                BEARER_AUTH,
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer"),
            )
            openApi.components(components)
        }

    private fun publicOpenApiCustomizer(): OpenApiCustomizer = OpenApiCustomizer { openApi: OpenAPI -> openApi.info(publicApiInfo()) }

    private fun publicApiInfo(): Info =
        Info()
            .title("Public API")
            .description("API used by public audience for searching authors and books.")
            .version("v1")
}
