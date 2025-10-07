package com.library.backend

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.DriverManager

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class RepositoryTestBase {
    companion object {
        @Container
        @JvmStatic
        val postgresContainer =
            PostgreSQLContainer<Nothing>("postgres:17.3").apply {
                withDatabaseName("testdb")
                withUsername("testuser")
                withPassword("testpassword")
                withReuse(true)
            }

        init {
            postgresContainer.start()
            waitForContainerReady(postgresContainer)
        }

        private fun waitForContainerReady(container: PostgreSQLContainer<*>) {
            val maxRetries = 10
            repeat(maxRetries) { _ ->
                try {
                    DriverManager.getConnection(container.jdbcUrl, container.username, container.password).use { conn ->
                        if (conn.isValid(2)) return
                    }
                } catch (_: Exception) {
                    Thread.sleep(1000)
                }
            }
            error("PostgreSQL container not reachable after $maxRetries retries")
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.datasource.driver-class-name") { postgresContainer.driverClassName }
        }
    }
}
