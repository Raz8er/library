package com.library.backend.service.author

import com.library.backend.entity.projection.AuthorWithPublishedBooksProjection
import com.library.backend.repository.AuthorRepository
import com.library.backend.utils.MapperUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AuthorCacheService(
    private val authorRepository: AuthorRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val mapper: MapperUtils,
) {
    private val cacheKeyPrefix = "author:with.published.books:"

    fun getCachedAuthorWithPublishedBooks(authorId: Long): AuthorWithPublishedBooksProjection? {
        val key = cacheKeyPrefix + authorId
        val cachedAuthor = redisTemplate.opsForValue().get(key)
        if (cachedAuthor != null) {
            return mapper.fromJson(mapper.toJson(cachedAuthor), AuthorWithPublishedBooksProjection::class.java)
        }
        val authorProjection = authorRepository.findAuthorWithNumberOfPublishedBooks(authorId) ?: return null
        redisTemplate.opsForValue().set(key, authorProjection, Duration.ofHours(1))
        return authorProjection
    }

    fun evictAuthor(authorId: Long) {
        redisTemplate.delete(cacheKeyPrefix + authorId)
    }
}
