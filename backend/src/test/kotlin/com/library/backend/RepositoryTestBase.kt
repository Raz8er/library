package com.library.backend

import com.library.backend.TestcontainersExtensions.waitForContainerReady
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@ComponentScan(basePackages = ["com.library.backend.creator"])
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
            postgresContainer.waitForContainerReady()
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
