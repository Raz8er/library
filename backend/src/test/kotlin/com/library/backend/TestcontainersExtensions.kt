package com.library.backend

import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import kotlin.use

object TestcontainersExtensions {
    fun PostgreSQLContainer<Nothing>.waitForContainerReady() {
        val maxRetries = 10
        repeat(maxRetries) { _ ->
            try {
                DriverManager.getConnection(this.jdbcUrl, this.username, this.password).use { conn ->
                    if (conn.isValid(2)) return
                }
            } catch (_: Exception) {
                Thread.sleep(1000)
            }
        }
        error("PostgreSQL container not reachable after $maxRetries retries")
    }
}
