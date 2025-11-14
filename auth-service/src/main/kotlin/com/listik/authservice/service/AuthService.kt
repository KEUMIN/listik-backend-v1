package com.listik.authservice.service

import com.listik.authservice.client.UserServiceClient
import com.listik.authservice.controller.dto.response.AuthResponse
import com.listik.authservice.controller.dto.response.UserDto
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.authservice.oauth.OAuth2AuthenticationManager
import com.listik.authservice.oauth.OAuth2ProviderType
import com.listik.authservice.oauth.exception.OAuth2TokenValidationException
import com.listik.authservice.refresh.model.RefreshToken
import com.listik.authservice.refresh.repository.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import org.springframework.beans.factory.annotation.Value

/**
 * 앱용 인증 서비스
 * - 앱은 OIDC Provider(Google/Apple)와 직접 통신하여 ID Token을 받음
 * - 백엔드는 ID Token을 검증하고 자체 JWT 토큰을 발급
 */
@Service
class AuthService(
    private val oauth2AuthenticationManager: OAuth2AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userServiceClient: UserServiceClient,
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${jwt.secret}") private val jwtSecret: String
) {

    /**
     * ID Token 검증 및 백엔드 토큰 발급
     *
     * @param idToken OIDC Provider로부터 받은 ID Token
     * @return 백엔드 Access Token, Refresh Token, 사용자 정보
     */
    fun verifyIdTokenAndIssueTokens(idToken: String): AuthResponse {
        // ID Token 검증 및 사용자 정보 추출
        // ID Token의 iss claim을 확인하여 provider 결정
        val providerType = determineProviderFromIdToken(idToken)

        // OAuth2 인증 처리 (ID Token 검증 + 사용자 조회/생성)
        val accessToken = oauth2AuthenticationManager.authenticate(providerType, idToken)

        // Access Token에서 userId 추출
        val userId = UUID.fromString(jwtTokenProvider.getEmail(accessToken))

        // 사용자 인증 계정 정보 조회
        val authAccountResponse = userServiceClient.findAuthAccountByProvider(
            provider = providerType.displayName,
            providerUserId = extractProviderUserId(idToken, providerType)
        )

        val authAccount = authAccountResponse.data
            ?: throw IllegalStateException("AuthAccount not found after authentication")

        // Refresh Token 생성 및 저장
        val refreshTokenValue = jwtTokenProvider.createRefreshToken()
        val refreshToken = RefreshToken(
            token = refreshTokenValue,
            userId = userId,
            provider = providerType.displayName,
            providerUserId = authAccount.providerUserId,
            ttl = jwtTokenProvider.getRefreshTokenExpiration() / 1000  // milliseconds to seconds
        )

        // 기존 Refresh Token 삭제 (단일 디바이스 정책)
        refreshTokenRepository.deleteByUserId(userId)

        // 새 Refresh Token 저장
        refreshTokenRepository.save(refreshToken)

        val accessTokenWithRole = jwtTokenProvider.createToken(userId.toString(), listOf(authAccount.role))

        return AuthResponse(
            accessToken = accessTokenWithRole,
            refreshToken = refreshTokenValue,
            user = UserDto(
                id = userId,
                provider = authAccount.provider,
                providerUserId = authAccount.providerUserId,
                nickName = authAccount.nickName
            )
        )
    }

    /**
     * Refresh Token으로 새로운 토큰 발급
     *
     * @param refreshTokenValue Refresh Token
     * @return 새로운 Access Token과 Refresh Token
     */
    fun refreshTokens(refreshTokenValue: String): AuthResponse {
        // Refresh Token 조회
        val refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            ?: throw IllegalArgumentException("Invalid refresh token")

        // 사용자 인증 계정 정보 조회 (현재 역할 정보 포함)
        val authAccountResponse = userServiceClient.findAuthAccountByProvider(
            provider = refreshToken.provider,
            providerUserId = refreshToken.providerUserId
        )

        val authAccount = authAccountResponse.data
            ?: throw IllegalStateException("AuthAccount not found for refresh")

        // 새로운 Access Token 생성 (현재 역할 포함)
        val newAccessToken = jwtTokenProvider.createToken(refreshToken.userId.toString(), listOf(authAccount.role))

        // 새로운 Refresh Token 생성
        val newRefreshTokenValue = jwtTokenProvider.createRefreshToken()
        val newRefreshToken = RefreshToken(
            token = newRefreshTokenValue,
            userId = refreshToken.userId,
            provider = refreshToken.provider,
            providerUserId = refreshToken.providerUserId,
            ttl = jwtTokenProvider.getRefreshTokenExpiration() / 1000
        )

        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByToken(refreshTokenValue)

        // 새 Refresh Token 저장
        refreshTokenRepository.save(newRefreshToken)

        // 응답 생성
        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshTokenValue,
            user = UserDto(
                id = refreshToken.userId,
                provider = refreshToken.provider,
                providerUserId = refreshToken.providerUserId,
                nickName = authAccount.nickName
            )
        )
    }

    /**
     * ID Token의 iss claim을 확인하여 provider 결정
     */
    private fun determineProviderFromIdToken(idToken: String): OAuth2ProviderType {
        try {
            // JWT 파싱하여 iss claim 확인 (검증 없이 디코딩만)
            val parts = idToken.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid ID token format")
            }

            val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
            val json = com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)
            val issuer = json.get("iss")?.asText()
                ?: throw IllegalArgumentException("ID token missing iss claim")

            return when {
                issuer.contains("accounts.google.com") -> OAuth2ProviderType.GOOGLE
                issuer.contains("appleid.apple.com") -> OAuth2ProviderType.APPLE
                else -> throw IllegalArgumentException("Unsupported issuer: $issuer")
            }
        } catch (e: Exception) {
            throw OAuth2TokenValidationException("Failed to determine provider from ID token", e)
        }
    }

    /**
     * ID Token에서 provider user ID 추출
     */
    private fun extractProviderUserId(idToken: String, providerType: OAuth2ProviderType): String {
        try {
            val parts = idToken.split(".")
            val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
            val json = com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)

            return json.get("sub")?.asText()
                ?: throw IllegalArgumentException("ID token missing sub claim")
        } catch (e: Exception) {
            throw OAuth2TokenValidationException("Failed to extract provider user ID", e)
        }
    }

    /**
     * 로그아웃 처리
     * 1. Refresh Token 삭제
     * 2. Access Token을 Redis 블랙리스트에 저장 (유효기간만큼)
     *
     * @param accessToken 블랙리스트에 저장할 Access Token
     */
    fun logout(accessToken: String) {
        try {
            // Access Token 검증 및 claims 추출
            val claims = getTokenClaims(accessToken)
            val userId = claims.subject
            val expirationTime = claims.expiration

            // 현재 시간과 만료 시간의 차이를 계산하여 TTL 설정
            val currentTime = System.currentTimeMillis()
            val remainingTimeMs = expirationTime.time - currentTime

            if (remainingTimeMs > 0) {
                // Redis 블랙리스트에 Access Token 저장
                // 키: blacklist:accessToken:{token}
                // 값: userId
                val blacklistKey = "blacklist:accessToken:$accessToken"
                redisTemplate.opsForValue().set(
                    blacklistKey,
                    userId,
                    remainingTimeMs,
                    TimeUnit.MILLISECONDS
                )
            }

            // Refresh Token 삭제 (userId로 조회하여 삭제)
            refreshTokenRepository.deleteByUserId(UUID.fromString(userId))

        } catch (e: Exception) {
            throw IllegalArgumentException("로그아웃 처리 중 오류 발생: ${e.message}", e)
        }
    }

    /**
     * Access Token의 claims를 추출합니다.
     */
    private fun getTokenClaims(token: String): Claims {
        val secretKey = SecretKeySpec(jwtSecret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            throw IllegalArgumentException("유효하지 않은 Access Token입니다.", e)
        }
    }
}
