package com.library.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val passwordEncoder: PasswordEncoder,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/public/**",
                        "/api/v2/public/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                    ).permitAll()
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            }.httpBasic(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val admin =
            User
                .builder()
                .username("admin")
                .password(passwordEncoder.encode("secret123"))
                .roles("ADMIN")
                .build()
        return InMemoryUserDetailsManager(admin)
    }
}
