package com.library.backend.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.user.admin")
data class AdminUser(
    val username: String,
    val password: String,
    val roles: List<String> = emptyList(),
)
