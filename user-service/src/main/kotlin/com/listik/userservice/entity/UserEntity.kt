package com.listik.userservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

/**
 * 최소한의 사용자 메타데이터만 보관
 * - 민감 정보(email, nickname 등)는 OAuth provider가 관리
 * - 앱 내 데이터 연결을 위한 UUID만 유지
 */
@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var tokenVersion: Int = 0,  // 강제 로그아웃을 위한 버전

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null
)
