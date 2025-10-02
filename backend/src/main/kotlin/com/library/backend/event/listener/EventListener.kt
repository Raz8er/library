package com.library.backend.event.listener

import com.library.backend.event.model.AuthorEvent
import com.library.backend.service.author.AuthorCacheService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EventListener(
    private val authorCacheService: AuthorCacheService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    fun onAuthorEvent(event: AuthorEvent) {
        logger.info("Received author event for author: ${event.id} -> evicting it from cache")
        authorCacheService.evictAuthor(event.id)
    }
}
