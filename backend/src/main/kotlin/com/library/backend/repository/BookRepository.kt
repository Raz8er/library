package com.library.backend.repository

import com.library.backend.dto.book.BookGenre
import com.library.backend.entity.BookEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BookRepository :
    JpaRepository<BookEntity, Long>,
    JpaSpecificationExecutor<BookEntity> {
    fun existsByIsbn(isbn: String): Boolean

    @EntityGraph(attributePaths = ["authors"])
    fun findByIsbn(isbn: String): BookEntity?

    @EntityGraph(attributePaths = ["authors"])
    fun findByIdIn(ids: Collection<Long>): Collection<BookEntity>

    @Query("SELECT b.isbn FROM BookEntity b WHERE b.publishingDateTime BETWEEN :startDateTime AND :endDateTime")
    fun findBookIsbnsByPublishingDateTimeBetween(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): Collection<String>

    @Query(
        """
        SELECT DISTINCT b 
        FROM BookEntity b
        JOIN b.authors a
        WHERE 
            (:title IS NULL OR LOWER(b.title) LIKE :title)
            AND (:isbn IS NULL OR b.isbn = :isbn)
            AND (:genre IS NULL OR b.genre = :genre)
            AND (:author IS NULL OR LOWER(a.name) LIKE :author)
            AND (
                CAST(:publishingDateTime AS timestamp) IS NULL
                OR b.publishingDateTime < :publishingDateTime
                OR (b.publishingDateTime = :publishingDateTime AND b.id < :id)
            )
        ORDER BY b.publishingDateTime DESC, b.id DESC
    """,
    )
    fun findBooksByFiltersAndCursor(
        title: String?,
        isbn: String?,
        genre: BookGenre?,
        author: String?,
        publishingDateTime: LocalDateTime?,
        id: Long?,
        pageable: Pageable,
    ): List<BookEntity>

    @EntityGraph(attributePaths = ["authors"])
    @Query("SELECT b FROM BookEntity b WHERE b IN (:bookIds)")
    fun findWithAuthors(bookIds: List<Long>): List<BookEntity>
}
