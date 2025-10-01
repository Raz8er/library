package com.library.authserver.utils

import kotlin.random.Random

object PasswordGenerator {
    private const val PASSWORD_LENGTH = 15
    private val specialChars: List<Char> = listOf('!', '?', ',', '-', '_')
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + specialChars

    fun generate(): String =
        (1..PASSWORD_LENGTH)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
}
