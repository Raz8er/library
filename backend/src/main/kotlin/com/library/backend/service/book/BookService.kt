package com.library.backend.service.book

import com.library.backend.dto.book.BookCreateDTO
import com.library.backend.dto.book.BookCursor
import com.library.backend.dto.book.BookFilter
import com.library.backend.dto.cursor.CursorPageRequest
import com.library.backend.dto.cursor.CursorPageResponse
import com.library.backend.entity.BookEntity
import com.library.backend.entity.specification.BookSpecification
import com.library.backend.mapper.BookMapper.toEntity
import com.library.backend.repository.BookRepository
import com.library.backend.service.author.AuthorCacheService
import com.library.backend.service.author.AuthorService
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class BookService(
    private val bookRepository: BookRepository,
    private val authorService: AuthorService,
    private val authorCacheService: AuthorCacheService,
) {
    @Transactional
    fun createBook(dto: BookCreateDTO): BookEntity {
        val entity = dto.toEntity()
        val isbn = entity.isbn!!
        if (bookRepository.existsByIsbn(isbn)) {
            return bookRepository.findByIsbn(isbn)!!
        }
        val authors = authorService.getAuthorsByIds(dto.authorIds!!)
        if (authors.isEmpty()) {
            throw EntityNotFoundException("Authors not found for ids: ${dto.authorIds}")
        }
        authors.forEach { entity.addAuthor(it) }
        authors.forEach { authorCacheService.evictAuthor(it.id!!) }
        return bookRepository.save(entity)
    }

    fun searchBooks(
        filter: BookFilter,
        pageable: Pageable,
    ): Page<BookEntity> {
        val spec = BookSpecification.withFilters(filter.title, filter.isbn, filter.genre, filter.author)
        val pageOfIds = bookRepository.findAll(spec, pageable).map { it.id!! }
        if (pageOfIds.isEmpty) {
            return Page.empty(pageable)
        }
        val books = bookRepository.findByIdIn(pageOfIds.content)
        val booksById = books.associateBy { it.id }
        val sortedBooks = pageOfIds.content.mapNotNull { booksById[it] }
        return PageImpl(sortedBooks, pageable, pageOfIds.totalElements)
    }

    fun searchBooksByCursor(
        filter: BookFilter,
        cursorPageRequest: CursorPageRequest,
    ): CursorPageResponse<BookEntity> {
        val cursor = cursorPageRequest.cursor?.let { BookCursor.decode(it) }
        val booksPage =
            bookRepository
                .findBooksByFiltersAndCursor(
                    title = filter.title?.let { "%${it.lowercase()}%" },
                    isbn = filter.isbn,
                    genre = filter.genre,
                    author = filter.author?.let { "%${it.lowercase()}%" },
                    publishingDateTime = cursor?.publishingDateTime ?: LocalDateTime.now().plusYears(100L),
                    id = cursor?.id ?: Long.MAX_VALUE,
                    pageable = PageRequest.of(0, cursorPageRequest.size!!),
                )
        val booksWithAuthors = getBooksWithAuthors(booksPage)
        val nextCursor = booksWithAuthors.lastOrNull()?.let { BookCursor(it.publishingDateTime, it.id!!).encode() }
        return CursorPageResponse(booksPage, nextCursor)
    }

    fun getBookIsbnsByPublishingDateTimeBetween(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
    ): List<String> = bookRepository.findBookIsbnsByPublishingDateTimeBetween(startDateTime, endDateTime).toList()

    private fun getBooksWithAuthors(booksPage: List<BookEntity>): List<BookEntity?> =
        if (booksPage.isNotEmpty()) {
            val booksById = bookRepository.findWithAuthors(booksPage.map { it.id!! }).associateBy { it.id!! }
            booksPage.map { booksById[it.id] }
        } else {
            emptyList()
        }
}
