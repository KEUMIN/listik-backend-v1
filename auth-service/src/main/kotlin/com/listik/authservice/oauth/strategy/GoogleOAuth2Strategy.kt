package com.listik.authservice.oauth.strategy

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.listik.authservice.oauth.OAuth2ProviderType
import com.listik.authservice.oauth.exception.OAuth2TokenValidationException
import com.listik.authservice.oauth.model.OAuth2UserInfo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleOAuth2Strategy(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientIds: String
) : OAuth2AuthenticationStrategy {

    private val logger = LoggerFactory.getLogger(GoogleOAuth2Strategy::class.java)

    override val providerType: OAuth2ProviderType = OAuth2ProviderType.GOOGLE

    override fun validateAndExtractUserInfo(idToken: String): OAuth2UserInfo {
        return try {
            val transport = NetHttpTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()

            val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(googleClientIds.split(","))
                .build()

            val googleIdToken: GoogleIdToken = verifier.verify(idToken)
                ?: throw OAuth2TokenValidationException("Invalid Google ID token: verification failed")

            val payload = googleIdToken.payload

            // 필수 클레임 검증
            val email = payload.email
                ?: throw OAuth2TokenValidationException("Google ID token missing email claim")
            val providerId = payload.subject
                ?: throw OAuth2TokenValidationException("Google ID token missing subject claim")

            // 이메일 검증 상태 확인
            val emailVerified = payload.emailVerified as? Boolean ?: false
            if (!emailVerified) {
                throw OAuth2TokenValidationException("Google account email is not verified")
            }

            val name = payload["name"] as? String

            logger.info("Successfully validated Google OAuth2 token for user: {}", email)

            OAuth2UserInfo(
                providerId = providerId,
                email = email,
                name = name,
                provider = OAuth2ProviderType.GOOGLE
            )

        } catch (ex: OAuth2TokenValidationException) {
            logger.warn("Google OAuth2 token validation failed: {}", ex.message)
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error during Google OAuth2 token validation", ex)
            throw OAuth2TokenValidationException("Failed to validate Google ID token", ex)
        }
    }
}