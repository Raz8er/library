package com.library.authserver.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
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
        private const val BASIC_AUTH = "basicAuth"
    }

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(
                Info().title("Library Auth Server API").version("1.0").description(
                    "API for requesting authorization tokens for library backend service",
                ),
            ).components(
                Components().apply {
                    addSecuritySchemes(
                        BASIC_AUTH,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                            .description("HTTP Basic authentication for client management endpoints (rotate secret)"),
                    )
                },
            )

    @Bean
    fun clientOpenApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("clients")
            .pathsToMatch("/api/v1/clients/**")
            .addOpenApiCustomizer(clientApiCustomizer())
            .build()

    @Bean
    fun tokenOpenApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("tokens")
            .pathsToMatch("/api/v1/token", "/api/v1/jwks.json")
            .addOpenApiCustomizer(tokenApiCustomizer())
            .build()

    private fun clientApiCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.info(
                Info()
                    .title("Client Management API")
                    .description("Register client (public) and rotate client secret (protected by Basic auth)")
                    .version("v1"),
            )
            val rotatePath = "/api/v1/clients/{clientId}/secret"
            val pathItem: PathItem? = openApi.paths?.get(rotatePath)
            pathItem?.post?.let { op ->
                val secReq = SecurityRequirement().addList(BASIC_AUTH)
                val existing = op.security
                op.security = (existing ?: emptyList()) + secReq
            }
        }

    private fun tokenApiCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            openApi.info(
                Info()
                    .title("Token API")
                    .description(
                        "Public token endpoints: /api/v1/token (client credentials in form body) and /api/v1/jwks.json (public keys)",
                    ).version("v1"),
            )
            val tokenPath = "/api/v1/token"
            val jwksPath = "/api/v1/jwks.json"
            listOf(tokenPath, jwksPath).forEach { p ->
                openApi.paths?.get(p)?.let { pathItem ->
                    pathItem.readOperations().forEach { it.security = null }
                }
            }
        }
}
