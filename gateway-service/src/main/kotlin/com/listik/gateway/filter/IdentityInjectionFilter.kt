package com.listik.gateway.filter

import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.annotation.Order
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 인증 정보를 하위 서비스로 전달하는 필터
 *
 * JWT 토큰에서 추출한 userId, roles, timezone 정보를 헤더에 추가하여
 * 하위 서비스에 전달합니다.
 *
 * Order 1로 설정하여 SecurityWebFilterChain (Order 0) 이후에 실행됩니다.
 */
@Component
@Order(1)
class IdentityInjectionFilter : GlobalFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return exchange.getPrincipal<JwtAuthenticationToken>()
            .flatMap { authentication ->
                val jwt = authentication.token
                val userId = jwt.claims["sub"] as String // "sub" 클레임을 userId로 사용

                val roles = authentication.authorities
                    .map { it.authority }
                    .joinToString(",")

                // 클라이언트에서 X-Timezone을 보낸 경우 사용, 없으면 기본값 (UTC)
                val timezone = exchange.request.headers["X-Timezone"]?.firstOrNull() ?: "UTC"

                val modifiedRequest = exchange.request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", roles)
                    .header("X-Timezone", timezone)
                    .build()

                val modifiedExchange = exchange.mutate().request(modifiedRequest).build()
                chain.filter(modifiedExchange)
            }
            .switchIfEmpty(
                chain.filter(exchange)
            )
    }
}