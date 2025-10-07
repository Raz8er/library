package com.library.backend.creator

import org.springframework.stereotype.Component

@Component
class TestCreator(
    private val authorCreator: AuthorCreator,
    private val bookCreator: BookCreator,
) {
    fun author(): AuthorCreator = authorCreator

    fun book(): BookCreator = bookCreator
}
