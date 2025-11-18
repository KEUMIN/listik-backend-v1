package com.listik.authservice.refresh.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import java.util.UUID

/**
 * Refresh Token Redis 저장 모델
 */
@RedisHash("refresh_token")
data class RefreshToken(
    @Id
    val token: String,

    @Indexed
    val userId: UUID,

    val provider: String,

    val providerUserId: String,

    @TimeToLive
    val ttl: Long,  // seconds

    val createdAt: Long = System.currentTimeMillis()
)
