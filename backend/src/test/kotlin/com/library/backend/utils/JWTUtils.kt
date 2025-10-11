package com.library.backend.utils

import com.library.backend.config.AdminUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.RequestPostProcessor

object JWTUtils {
    fun getAdminJWT(): SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor =
        jwt().authorities(SimpleGrantedAuthority("SCOPE_admin"))

    fun getTestUser(testUser: TestUser): SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor =
        user(testUser.username).password(testUser.password).roles(*testUser.roles.toTypedArray())

    fun getAdminUser(adminUser: AdminUser): SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor =
        user(adminUser.username).password(adminUser.password).roles(*adminUser.roles.toTypedArray())

    fun getAnonymousUser(): RequestPostProcessor = anonymous()

    fun getAuthorJWT(): SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor =
        jwt().authorities(SimpleGrantedAuthority("SCOPE_author"))
}
