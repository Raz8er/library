package com.library.backend.creator

import java.security.SecureRandom

object BookISBNGenerator {
    fun generateISBN(): String = "${digits(3)}-${digits(2)}-${digits(5)}-${digits(2)}-${digits(1)}"

    private fun digits(count: Int) = (1..count).joinToString("") { SecureRandom().nextInt(0, 10).toString() }
}
