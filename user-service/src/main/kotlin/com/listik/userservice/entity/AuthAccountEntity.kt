package com.listik.userservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(
    name = "auth_accounts",
    indexes = [Index(name = "idx_oauth_user_id", columnList = "user_id")],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_oauth_provider_sub",
            columnNames = ["provider", "provider_user_id"]
        ),
        UniqueConstraint(
            name = "uk_oauth_local_email",
            columnNames = ["email"]
        )
    ]
)
class AuthAccountEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @Column(nullable = true, length = 256)
    var email: String? = null,

    @Column(nullable = true, length = 256)
    var passwordHash: String? = null,

    @Column(nullable = false, length = 20)
    var provider: String? = null,             // "GOOGLE", "APPLE"

    @Column(nullable = false, length = 191)
    var providerUserId: String? = null,

    @CreationTimestamp
    var createdAt: Instant? = null,

    @UpdateTimestamp
    var updatedAt: Instant? = null
) {
    constructor() : this(
        user = UserEntity()
    )
}
