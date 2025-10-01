package com.library.backend.entity.specification

import com.library.backend.dto.book.BookCursor
import com.library.backend.dto.book.BookGenre
import com.library.backend.dto.cursor.CursorDirection
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.BookEntity
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

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

    fun withCursor(
        cursor: BookCursor?,
        direction: CursorDirection,
    ): Specification<BookEntity> =
        Specification { root, _, cb ->
            if (cursor == null) {
                return@Specification cb.conjunction()
            }
            val publishingDateTime = root.get<LocalDateTime>("publishingDateTime")
            val id = root.get<Long>("id")

            when (direction) {
                CursorDirection.FORWARD ->
                    cb.or(
                        cb.lessThan(publishingDateTime, cursor.publishingDateTime),
                        cb.and(
                            cb.equal(publishingDateTime, cursor.publishingDateTime),
                            cb.lessThan(id, cursor.id),
                        ),
                    )
                CursorDirection.BACKWARD ->
                    cb.or(
                        cb.greaterThan(publishingDateTime, cursor.publishingDateTime),
                        cb.and(
                            cb.equal(publishingDateTime, cursor.publishingDateTime),
                            cb.greaterThan(id, cursor.id),
                        ),
                    )
            }
        }
}
