package com.library.backend.creator

object TestCreator {
    fun author(): AuthorCreator = AuthorCreator()

    fun book(): BookCreator = BookCreator()
}
