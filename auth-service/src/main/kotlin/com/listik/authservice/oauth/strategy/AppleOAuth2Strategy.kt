package com.listik.authservice.oauth.strategy

import com.listik.authservice.oauth.OAuth2ProviderType
import com.listik.authservice.oauth.exception.OAuth2TokenValidationException
import com.listik.authservice.oauth.model.OAuth2UserInfo
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URL
import java.time.Instant

@Component
class AppleOAuth2Strategy(
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val appleClientId: String
) : OAuth2AuthenticationStrategy {

    private val logger = LoggerFactory.getLogger(AppleOAuth2Strategy::class.java)

    override val providerType: OAuth2ProviderType = OAuth2ProviderType.APPLE

    companion object {
        private const val APPLE_ISSUER = "https://appleid.apple.com"
        private const val APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys"
    }

    override fun validateAndExtractUserInfo(idToken: String): OAuth2UserInfo {
        return try {
            val claims: JWTClaimsSet = getJwtProcessor().process(idToken, null)

            // 필수 클레임 검증
            validateRequiredClaims(claims)

            val providerId = claims.subject
                ?: throw OAuth2TokenValidationException("Apple ID token missing subject claim")

            // Apple은 이메일을 선택적으로 제공하므로 fallback 처리
            val email = claims.getStringClaim("email")
                ?: generateFallbackEmail(providerId)

            // Apple은 이름 정보를 ID 토큰에 포함하지 않으므로 빈 문자열로 설정
            val name = claims.getStringClaim("name") ?: ""

            logger.info("Successfully validated Apple OAuth2 token for user: {}", email)

            OAuth2UserInfo(
                providerId = providerId,
                email = email,
                name = name.ifEmpty { null },
                provider = OAuth2ProviderType.APPLE
            )

        } catch (ex: OAuth2TokenValidationException) {
            logger.warn("Apple OAuth2 token validation failed: {}", ex.message)
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error during Apple OAuth2 token validation", ex)
            throw OAuth2TokenValidationException("Failed to validate Apple ID token", ex)
        }
    }

    private fun getJwtProcessor(): ConfigurableJWTProcessor<SecurityContext> {
        val jwkSet = JWKSet.load(URL(APPLE_KEYS_URL))
        val jwkSource: JWKSource<SecurityContext> = ImmutableJWKSet(jwkSet)

        val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
        val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)
        jwtProcessor.jwsKeySelector = keySelector
        return jwtProcessor
    }

    private fun validateRequiredClaims(claims: JWTClaimsSet) {
        // Issuer 검증
        if (claims.issuer != APPLE_ISSUER) {
            throw OAuth2TokenValidationException("Invalid issuer: ${claims.issuer}")
        }

        // Audience 검증
        if (!claims.audience.contains(appleClientId)) {
            throw OAuth2TokenValidationException("Invalid audience: ${claims.audience}")
        }

        // 토큰 만료 시간 검증
        val now = Instant.now()
        if (claims.expirationTime?.toInstant()?.isBefore(now) == true) {
            throw OAuth2TokenValidationException("Apple ID token has expired")
        }

        // 토큰 발급 시간 검증 (너무 오래된 토큰 거부)
        if (claims.issueTime?.toInstant()?.isBefore(now.minusSeconds(3600)) == true) {
            throw OAuth2TokenValidationException("Apple ID token is too old")
        }
    }

    private fun generateFallbackEmail(providerId: String): String {
        return "user-$providerId@apple.local"
    }
}