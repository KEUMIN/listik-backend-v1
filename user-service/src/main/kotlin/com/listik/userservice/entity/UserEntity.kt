package com.listik.userservice.entity

import com.listik.coreservice.entity.BaseEntity
import jakarta.persistence.*
import java.util.*

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

    var nickName: String? = null,

    ) : BaseEntity()
