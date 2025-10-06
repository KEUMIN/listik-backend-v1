package com.listik.authservice.oauth.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

/**
 * Apple OAuth2를 위한 JWT 생성 서비스 (private_key_jwt 방식)
 */
@Service
class AppleJwtService(
    @Value("\${apple.team-id:}")
    private val teamId: String,

    @Value("\${apple.key-id:}")
    private val keyId: String,

    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,

    @Value("\${apple.private-key-path:classpath:apple-private-key.p8}")
    private val privateKeyResource: Resource
) {

    private val privateKey: PrivateKey? by lazy {
        try {
            if (privateKeyResource.exists()) {
                loadPrivateKey()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Apple token endpoint를 위한 client_secret JWT 생성
     */
    fun generateClientSecret(): String {
        if (teamId.isBlank() || keyId.isBlank()) {
            throw IllegalStateException(
                "Apple OAuth2 configuration missing: apple.team-id and apple.key-id required"
            )
        }

        val key = privateKey ?: throw IllegalStateException(
            "Apple private key not found at: ${privateKeyResource.description}"
        )

        val now = Date()
        val expirationTime = Date(now.time + 3600000) // 1시간

        return Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(teamId)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .signWith(key, SignatureAlgorithm.ES256)
            .compact()
    }

    /**
     * Apple private key 파일 로드 (.p8 형식)
     */
    private fun loadPrivateKey(): PrivateKey {
        val content = privateKeyResource.inputStream.bufferedReader().use { it.readText() }

        // PEM 헤더/푸터 제거
        val privateKeyPEM = content
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val encoded = Base64.getDecoder().decode(privateKeyPEM)
        val keySpec = PKCS8EncodedKeySpec(encoded)
        val keyFactory = KeyFactory.getInstance("EC")
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * Apple OAuth2가 설정되어 있는지 확인
     */
    fun isConfigured(): Boolean {
        return teamId.isNotBlank() &&
               keyId.isNotBlank() &&
               privateKey != null
    }
}
