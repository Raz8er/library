package com.library.backend.testbase

import org.testcontainers.containers.PostgreSQLContainer

object PostgresContainer : PostgreSQLContainer<PostgresContainer>("postgres:17.3") {
    init {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpassword")
        withReuse(true)
        start()
    }
}
