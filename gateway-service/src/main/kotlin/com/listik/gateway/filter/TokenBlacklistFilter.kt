package com.listik.gateway.filter

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * 토큰 블랙리스트 검증 필터
 *
 * 로그아웃 한 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
 * 블랙리스트에 있는 토큰은 요청을 거부합니다.
 */
@Component
class TokenBlacklistFilter(
    private val redisTemplate: RedisTemplate<String, String>
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // 인증이 필요하지 않은 경로는 스킵
        val path = exchange.request.path.value()
        if (isPublicPath(path)) {
            return chain.filter(exchange)
        }

        // 현재 인증 정보 확인
        return exchange.getPrincipal<Authentication>()
            .flatMap { authentication ->
                // JwtAuthenticationToken인 경우 처리
                if (authentication is JwtAuthenticationToken) {
                    val jwt = authentication.token
                    val tokenValue = jwt.tokenValue

                    // 토큰이 블랙리스트에 있는지 확인
                    if (isTokenBlacklisted(tokenValue)) {
                        // 블랙리스트된 토큰으로 요청 시도
                        return@flatMap Mono.error(
                            BadCredentialsException("로그아웃 된 토큰입니다.")
                        )
                    }
                }

                chain.filter(exchange)
            }
            .switchIfEmpty(chain.filter(exchange))
    }

    /**
     * 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
     */
    private fun isTokenBlacklisted(token: String): Boolean {
        val blacklistKey = "blacklist:accessToken:$token"
        return redisTemplate.hasKey(blacklistKey) == true
    }

    /**
     * 인증 검증이 필요 없는 경로인지 확인합니다.
     */
    private fun isPublicPath(path: String): Boolean {
        val publicPaths = listOf(
            "/api/v1/auth/verify",
            "/api/v1/auth/refresh",
            "/actuator/health"
        )
        return publicPaths.any { path.startsWith(it) }
    }
}
