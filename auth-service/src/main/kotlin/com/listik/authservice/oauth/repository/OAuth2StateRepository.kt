package com.listik.authservice.oauth.repository

import com.listik.authservice.oauth.model.OAuth2State
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

/**
 * OAuth2 State 정보를 Redis에 저장/조회하는 Repository
 */
@Repository
class OAuth2StateRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private const val KEY_PREFIX = "oauth2:state:"
        private const val TTL_MINUTES = 10L // OAuth2 흐름이 완료될 때까지 충분한 시간
    }

    /**
     * OAuth2 state 저장 (TTL: 10분)
     */
    fun save(state: OAuth2State) {
        val key = buildKey(state.stateId)
        redisTemplate.opsForValue().set(key, state, TTL_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * State ID로 OAuth2 state 조회
     */
    fun findByStateId(stateId: String): OAuth2State? {
        val key = buildKey(stateId)
        return redisTemplate.opsForValue().get(key) as? OAuth2State
    }

    /**
     * State ID로 OAuth2 state 삭제
     */
    fun deleteByStateId(stateId: String) {
        val key = buildKey(stateId)
        redisTemplate.delete(key)
    }

    private fun buildKey(stateId: String): String {
        return "$KEY_PREFIX$stateId"
    }
}
