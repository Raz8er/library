package com.library.backend.testbase

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.backend.config.AdminUser
import com.library.backend.config.PasswordEncoderConfig
import com.library.backend.config.SecurityConfig
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(AdminUser::class)
@Import(SecurityConfig::class, PasswordEncoderConfig::class)
abstract class ControllerTestBase {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper
}
