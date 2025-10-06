package com.listik.authservice.pkce.repository

import com.listik.authservice.pkce.model.PkceSession
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

/**
 * PKCE 세션 정보를 Redis에 저장/조회하는 Repository
 */
@Repository
class PkceSessionRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private const val KEY_PREFIX = "pkce:"
        private const val TTL_MINUTES = 5L
    }

    /**
     * PKCE 세션 저장 (TTL: 5분)
     */
    fun save(session: PkceSession) {
        val key = buildKey(session.authorizationCode)
        redisTemplate.opsForValue().set(key, session, TTL_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * Authorization code로 PKCE 세션 조회
     */
    fun findByAuthorizationCode(authorizationCode: String): PkceSession? {
        val key = buildKey(authorizationCode)
        return redisTemplate.opsForValue().get(key) as? PkceSession
    }

    /**
     * Authorization code로 PKCE 세션 삭제
     */
    fun deleteByAuthorizationCode(authorizationCode: String) {
        val key = buildKey(authorizationCode)
        redisTemplate.delete(key)
    }

    private fun buildKey(authorizationCode: String): String {
        return "$KEY_PREFIX$authorizationCode"
    }
}
