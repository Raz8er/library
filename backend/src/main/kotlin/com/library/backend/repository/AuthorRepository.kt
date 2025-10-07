package com.library.backend.repository

import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Repository
interface AuthorRepository : JpaRepository<AuthorEntity, Long> {
    @Query("SELECT a FROM AuthorEntity a LEFT JOIN FETCH a.books WHERE a.id IN (:authorIds)")
    fun findAllByAuthorIds(authorIds: Collection<Long>): Collection<AuthorEntity>

    @Query(
        """
        SELECT
            new com.library.backend.entity.projection.AuthorWithPublishedBooksProjection(
                a.id AS id,
                a.name AS name,
                a.dateOfBirth AS dateOfBirth,
                a.createdAt AS createdAt,
                (
                    SELECT
                        count(*)
                    FROM
                        a.books
                ) AS numberOfPublishedBooks
            )
        FROM
            AuthorEntity a
    """,
    )
    fun findAuthorsWithNumberOfPublishedBooks(pageable: Pageable): Page<AuthorWithPublishedBooksProjection>

    @Query(
        """
        SELECT
            new com.library.backend.entity.projection.AuthorWithPublishedBooksProjection(
                a.id AS id,
                a.name AS name,
                a.dateOfBirth AS dateOfBirth,
                a.createdAt AS createdAt,
                (
                    SELECT
                        count(*)
                    FROM
                        a.books
                ) AS numberOfPublishedBooks
            )
        FROM
            AuthorEntity a
        WHERE a.id = :authorId
    """,
    )
    fun findAuthorWithNumberOfPublishedBooks(authorId: Long): AuthorWithPublishedBooksProjection?

    @Query(
        """
        SELECT new com.library.backend.entity.projection.AuthorWithPublishedBooksProjection(
            a.id AS id,
            a.name AS name, 
            a.dateOfBirth AS dateOfBirth,
            a.createdAt AS createdAt,
            (
                SELECT 
                    count(b) 
                FROM 
                    a.books b
            ) AS numberOfPublishedBooks
        )
        FROM AuthorEntity a
        WHERE (
            CAST(:createdAt AS TIMESTAMP) IS NULL
            OR a.createdAt < :createdAt
            OR (
                a.createdAt = :createdAt 
                AND (
                    :authorId IS NULL
                    OR a.id < :authorId
                )
            )
        )
        ORDER BY a.createdAt DESC, a.id DESC
    """,
    )
    fun findAuthorsByCreatedAtCursor(
        createdAt: ZonedDateTime?,
        authorId: Long?,
        pageable: Pageable,
    ): Page<AuthorWithPublishedBooksProjection>

    @Query(
        """
        SELECT new com.library.backend.entity.projection.AuthorWithPublishedBooksProjection(
            a.id AS id,
            a.name AS name, 
            a.dateOfBirth AS dateOfBirth,
            a.createdAt AS createdAt,
            COALESCE(bc.bookCount, 0) AS numberOfPublishedBooks
        )
        FROM AuthorEntity a
        LEFT JOIN (
            SELECT ab.id AS authorId, COUNT(ab) AS bookCount
            FROM BookEntity b
            JOIN b.authors ab
            GROUP BY ab.id
        ) bc ON a.id = bc.authorId
        WHERE (
            :numberOfPublishedBooks IS NULL
            OR bc.bookCount < :numberOfPublishedBooks
            OR (
                bc.bookCount = :numberOfPublishedBooks 
                AND (
                    :authorId IS NULL
                    OR a.id < :authorId
                )
            )
        )
        ORDER BY bc.bookCount DESC, a.id DESC
    """,
    )
    fun findAuthorsByNumberOfPublishedBooksCursor(
        numberOfPublishedBooks: Long?,
        authorId: Long?,
        pageable: Pageable,
    ): Page<AuthorWithPublishedBooksProjection>
}
