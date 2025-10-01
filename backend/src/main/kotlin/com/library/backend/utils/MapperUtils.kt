package com.library.backend.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class MapperUtils(
    private val mapper: ObjectMapper,
) {
    fun <T> fromJson(
        json: String,
        clazz: Class<T>,
    ): T = mapper.readValue(json, clazz)

    fun toJson(any: Any): String = mapper.writeValueAsString(any)
}
