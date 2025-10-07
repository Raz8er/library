package com.library.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: ZonedDateTime? = null,
    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime? = null,
) {
    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = ZonedDateTime.now()
        }
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = ZonedDateTime.now()
    }
}
