package com.listik.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/v1/auth/**").permitAll()
                    .pathMatchers("/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }
            .build()
    }

    @Bean
    fun reactiveJwtDecoder(@Value("\${jwt.secret}") secret: String): ReactiveJwtDecoder {
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build()
    }

    @Bean
    @Order(-1)
    fun identityInjectionFilter(): GlobalFilter {
        return GlobalFilter { exchange, chain ->
            exchange.getPrincipal<JwtAuthenticationToken>()
                .flatMap { authentication ->
                    val jwt = authentication.token
                    val userId = jwt.claims["sub"] as String // "sub" 클레임을 userId로 사용

                    val roles = authentication.authorities
                        .map { it.authority }
                        .joinToString(",")

                    val modifiedRequest = exchange.request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Roles", roles)
                        .build()

                    val modifiedExchange = exchange.mutate().request(modifiedRequest).build()
                    chain.filter(modifiedExchange)
                }
                .switchIfEmpty(
                    chain.filter(exchange)
                )
        }
    }
}