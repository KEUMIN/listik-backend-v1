package com.listik.gateway.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

/**
 * JWT에서 roles 클레임을 authorities로 변환하는 컨버터
 */
class JwtToAuthenticationConverter : Converter<Jwt, Mono<JwtAuthenticationToken>> {
    override fun convert(jwt: Jwt): Mono<JwtAuthenticationToken> {
        val roles = jwt.getClaimAsStringList("roles") ?: emptyList()
        val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }

        val token = JwtAuthenticationToken(jwt, authorities, jwt.claims["sub"].toString())
        return Mono.just(token)
    }
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/api/v1/auth/verify").permitAll()
                    .pathMatchers("/api/v1/auth/refresh").permitAll()
                    .pathMatchers("/api/v1/auth/logout").authenticated()
                    .pathMatchers("/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .build()
    }

    @Bean
    fun reactiveJwtDecoder(@Value("\${jwt.secret}") secret: String): ReactiveJwtDecoder {
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build()
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, Mono<JwtAuthenticationToken>> {
        return JwtToAuthenticationConverter()
    }
}