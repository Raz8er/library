package com.library.backend

import com.library.backend.TestcontainersExtensions.waitForContainerReady
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {
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

        @Container
        @JvmStatic
        val redisContainer =
            GenericContainer<Nothing>("redis:8.0.4").apply {
                withExposedPorts(6379)
                withReuse(true)
            }

        init {
            postgresContainer.start()
            redisContainer.start()
            postgresContainer.waitForContainerReady()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.datasource.driver-class-name") { postgresContainer.driverClassName }
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.firstMappedPort }
        }
    }
}
