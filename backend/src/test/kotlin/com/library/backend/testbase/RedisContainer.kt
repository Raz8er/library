package com.library.backend.testbase

import org.testcontainers.containers.GenericContainer

object RedisContainer : GenericContainer<RedisContainer>("redis:8.0.4") {
    init {
        withExposedPorts(6379)
        withReuse(true)
        start()
    }
}
