package com.listik.userservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

/**
 * OAuth provider 연결 정보만 보관
 * - email, password 등 민감 정보는 저장하지 않음
 * - provider + providerUserId로 중복 가입 방지
 */
@Entity
@Table(
    name = "auth_accounts",
    indexes = [Index(name = "idx_oauth_user_id", columnList = "user_id")],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_oauth_provider_sub",
            columnNames = ["provider", "provider_user_id"]
        )
    ]
)
class AuthAccountEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity = UserEntity(),

    @Column(nullable = false, length = 20)
    var provider: String = "",  // "GOOGLE", "APPLE"

    @Column(nullable = false, length = 191, name = "provider_user_id")
    var providerUserId: String = "",  // OAuth provider의 sub claim

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: Role = Role.USER,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null
)
