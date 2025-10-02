package com.library.backend.entity.specification

import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object BookSpecification {
    fun withFilters(
        title: String? = null,
        isbn: String? = null,
        genre: BookGenre? = null,
        author: String? = null,
    ): Specification<BookEntity> =
        Specification<BookEntity> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            title?.let {
                predicates.add(cb.like(cb.lower(root.get("title")), "%${it.lowercase()}%"))
            }
            isbn?.let {
                predicates.add(cb.equal(root.get<String>("isbn"), it))
            }
            genre?.let {
                predicates.add(cb.equal(cb.lower(root.get("genre")), it.text.lowercase()))
            }
            author?.let {
                val authors = root.join<BookEntity, AuthorEntity>("authors", JoinType.LEFT)
                predicates.add(cb.like(cb.lower(authors.get("name")), "%${it.lowercase()}%"))
            }
            cb.and(*predicates.toTypedArray())
        }
}
