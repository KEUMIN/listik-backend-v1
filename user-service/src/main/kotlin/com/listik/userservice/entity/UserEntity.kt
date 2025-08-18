package com.listik.userservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "users",
    indexes = [Index(name = "idx_users_email", columnList = "email")],
)
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = true, length = 256)
    var email: String? = null,

    @Column(nullable = false)
    var emailVerified: Boolean = false,

    @Column(nullable = false, length = 100)
    var nickname: String = "",

    @Column(nullable = false, length = 20)
    var role: String = "USER",

    @Column(nullable = false)
    var tokenVersion: Int = 0,

    @Column(length = 20)
    var locale: String? = null,       // e.g. "ko-KR"

    @Column(length = 50)
    var timeZone: String? = null,     // e.g. "Asia/Seoul"

    var lastLoginAt: Instant? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant? = null
)
