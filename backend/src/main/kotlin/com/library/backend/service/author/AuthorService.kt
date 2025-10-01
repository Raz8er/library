package com.library.backend.service.author

import com.library.backend.dto.author.AuthorCreateDTO
import com.library.backend.dto.author.AuthorCursor
import com.library.backend.dto.author.AuthorSort
import com.library.backend.dto.author.AuthorUpdateDTO
import com.library.backend.dto.cursor.CursorPageRequest
import com.library.backend.dto.cursor.CursorPageResponse
import com.library.backend.entity.AuthorEntity
import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import com.library.backend.mapper.AuthorMapper.toEntity
import com.library.backend.mapper.AuthorMapper.updateEntity
import com.library.backend.repository.AuthorRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
@Transactional(readOnly = true)
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val authorCacheService: AuthorCacheService,
) {
    @Transactional
    fun createAuthor(author: AuthorCreateDTO): AuthorEntity = authorRepository.save(author.toEntity())

    @Transactional
    fun updateAuthor(
        authorId: Long,
        dto: AuthorUpdateDTO,
    ): AuthorEntity {
        val existingEntity =
            authorRepository
                .findById(
                    authorId,
                ).orElseThrow { EntityNotFoundException("Author with id $authorId not found") }
        val updatedEntity = dto.toEntity()
        authorCacheService.evictAuthor(authorId)
        return authorRepository.save(existingEntity.updateEntity(updatedEntity))
    }

    fun getAuthorsByIds(authorIds: Collection<Long>): MutableSet<AuthorEntity> =
        authorRepository.findAllByAuthorIds(authorIds).toMutableSet()

    fun getAuthors(pageable: Pageable): Page<AuthorWithPublishedBooksProjection> {
        val authors = authorRepository.findAuthorsWithNumberOfPublishedBooks(pageable)
        val cachedAuthors = authors.content.mapNotNull { authorCacheService.getCachedAuthorWithPublishedBooks(it.id) }
        val result = cachedAuthors.ifEmpty { authors.toList() }
        return PageImpl(result, pageable, authors.totalElements)
    }

    fun getAuthorsCursor(cursorPageRequest: CursorPageRequest): CursorPageResponse<AuthorWithPublishedBooksProjection> {
        val cursor = cursorPageRequest.cursor?.let { AuthorCursor.decode(it) }
        val pageSize = cursorPageRequest.size!!
        val sortBy = AuthorSort.getEnumValue(cursorPageRequest.sortBy!!)
        val authors = getAuthorsCursor(sortBy, cursor, pageSize)

        val nextCursor =
            authors.lastOrNull()?.let {
                AuthorCursor(
                    createdAt = it.createdAt.toLocalDateTime(),
                    numberOfPublishedBooks = it.numberOfPublishedBooks,
                    id = it.id,
                ).encode()
            }

        return CursorPageResponse(authors, nextCursor)
    }

    private fun getAuthorsCursor(
        sortBy: AuthorSort,
        cursor: AuthorCursor?,
        pageSize: Int,
    ): List<AuthorWithPublishedBooksProjection> =
        when (sortBy) {
            AuthorSort.BOOKS -> {
                authorRepository.findAuthorsByNumberOfPublishedBooksCursor(
                    numberOfPublishedBooks = cursor?.numberOfPublishedBooks ?: Long.MAX_VALUE,
                    authorId = cursor?.id ?: Long.MAX_VALUE,
                    pageable = PageRequest.of(0, pageSize),
                )
            }

            AuthorSort.CREATED_AT ->
                authorRepository.findAuthorsByCreatedAtCursor(
                    createdAt =
                        cursor?.createdAt?.let { ZonedDateTime.of(it, ZoneId.systemDefault()) }
                            ?: ZonedDateTime.now(ZoneId.systemDefault()).plusYears(100),
                    authorId = cursor?.id ?: Long.MAX_VALUE,
                    pageable = PageRequest.of(0, pageSize),
                )
        }
}
