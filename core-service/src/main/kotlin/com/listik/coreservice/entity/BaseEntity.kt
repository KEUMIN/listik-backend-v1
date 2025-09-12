package com.listik.coreservice.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedBy
    @Column(updatable = false)
    open var createdBy: String? = null
        protected set

    @LastModifiedBy
    open var updatedBy: String? = null
        protected set

    @CreatedDate
    @Column(updatable = false)
    open var createdAt: Instant? = null
        protected set

    @LastModifiedDate
    open var updatedAt: Instant? = null
        protected set

    @PrePersist
    fun prePersist() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}