package com.library.backend.event.service

import com.library.backend.event.model.AuthorEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventNotifier(
    private val publisher: ApplicationEventPublisher,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishAuthorEvent(authorId: Long) {
        logger.info("Publishing author event for author: $authorId")
        publisher.publishEvent(AuthorEvent(authorId))
    }
}
