package com.listik.authservice.service

import com.listik.authservice.client.UserServiceClient
import com.listik.authservice.controller.dto.response.OidcTokenResponse
import com.listik.authservice.controller.dto.response.UserInfoResponse
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.authservice.oauth.OAuth2AuthenticationManager
import com.listik.authservice.oauth.OAuth2ProviderType
import com.listik.authservice.oauth.model.OAuth2State
import com.listik.authservice.oauth.repository.OAuth2StateRepository
import com.listik.authservice.oauth.service.AppleJwtService
import com.listik.authservice.pkce.model.PkceSession
import com.listik.authservice.pkce.repository.PkceSessionRepository
import com.listik.authservice.pkce.service.PkceService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.security.SecureRandom
import java.util.*

/**
 * OIDC + PKCE 브로커 서비스
 */
@Service
class OidcService(
    private val pkceService: PkceService,
    private val pkceSessionRepository: PkceSessionRepository,
    private val oauth2StateRepository: OAuth2StateRepository,
    private val oauth2AuthenticationManager: OAuth2AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userServiceClient: UserServiceClient,
    private val appleJwtService: AppleJwtService,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private val googleClientSecret: String,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val appleClientId: String,
    @Value("\${spring.security.oauth2.authorizationserver.issuer}")
    private val issuer: String,
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long
) {

    private val restTemplate = RestTemplate()
    private val secureRandom = SecureRandom()

    companion object {
        private const val GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val APPLE_AUTH_URL = "https://appleid.apple.com/auth/authorize"
        private const val APPLE_TOKEN_URL = "https://appleid.apple.com/auth/token"
    }

    /**
     * Provider의 authorization URL 생성
     * Redis에 state 정보를 저장하여 CSRF 방지 및 파라미터 전달
     */
    fun buildAuthorizationUrl(
        provider: String,
        redirectUri: String,
        state: String?,
        codeChallenge: String,
        codeChallengeMethod: String
    ): String {
        // 랜덤 state ID 생성 (CSRF 방지)
        val stateId = generateSecureStateId()

        // OAuth2 state 정보를 Redis에 저장
        val oauth2State = OAuth2State(
            stateId = stateId,
            clientState = state,
            redirectUri = redirectUri,
            codeChallenge = codeChallenge,
            codeChallengeMethod = codeChallengeMethod,
            provider = provider,
            createdAt = System.currentTimeMillis()
        )
        oauth2StateRepository.save(oauth2State)

        return when (provider.lowercase()) {
            "google" -> buildGoogleAuthUrl(stateId)
            "apple" -> buildAppleAuthUrl(stateId)
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }
    }

    /**
     * Provider callback 처리
     * Redis에서 state 정보를 조회하여 PKCE 파라미터 복원
     */
    fun handleProviderCallback(
        provider: String,
        providerCode: String,
        stateId: String?
    ): CallbackResult {
        // Redis에서 OAuth2 state 조회
        val oauth2State = oauth2StateRepository.findByStateId(stateId ?: "")
            ?: throw IllegalArgumentException("Invalid or expired state parameter (CSRF attack possible)")

        // Provider 검증
        if (oauth2State.provider != provider) {
            throw IllegalArgumentException("Provider mismatch in state")
        }

        // State 사용 후 즉시 삭제 (재사용 방지)
        oauth2StateRepository.deleteByStateId(oauth2State.stateId)

        // Provider의 authorization code를 ID token으로 교환
        val idToken = exchangeProviderCodeForToken(provider, providerCode)

        // ID token 검증 및 사용자 정보 추출 + JWT 토큰 발급
        val providerType = when (provider.lowercase()) {
            "google" -> OAuth2ProviderType.GOOGLE
            "apple" -> OAuth2ProviderType.APPLE
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }
        val jwtToken = oauth2AuthenticationManager.authenticate(providerType, idToken)

        // JWT 토큰에서 사용자 이메일 추출
        val email = jwtTokenProvider.getEmail(jwtToken)

        // 자체 authorization code 생성
        val authorizationCode = pkceService.generateAuthorizationCode()

        // PKCE 세션 저장
        val pkceSession = PkceSession(
            authorizationCode = authorizationCode,
            codeChallenge = oauth2State.codeChallenge,
            codeChallengeMethod = oauth2State.codeChallengeMethod,
            redirectUri = oauth2State.redirectUri,
            state = oauth2State.clientState,
            email = email,
            provider = provider,
            createdAt = System.currentTimeMillis()
        )
        pkceSessionRepository.save(pkceSession)

        return CallbackResult(
            authorizationCode = authorizationCode,
            redirectUri = oauth2State.redirectUri,
            clientState = oauth2State.clientState
        )
    }

    /**
     * Authorization code를 JWT token으로 교환 (PKCE 검증)
     */
    fun exchangeCodeForToken(
        authorizationCode: String,
        redirectUri: String,
        codeVerifier: String
    ): OidcTokenResponse {
        // PKCE 세션 조회
        val pkceSession = pkceSessionRepository.findByAuthorizationCode(authorizationCode)
            ?: throw IllegalArgumentException("Invalid authorization code")

        // Redirect URI 검증
        if (pkceSession.redirectUri != redirectUri) {
            throw IllegalArgumentException("Invalid redirect_uri")
        }

        // PKCE 검증
        if (!pkceService.verifyCodeChallenge(codeVerifier, pkceSession.codeChallenge, pkceSession.codeChallengeMethod)) {
            throw IllegalArgumentException("Invalid code_verifier")
        }

        // JWT 토큰 생성
        val accessToken = jwtTokenProvider.createToken(pkceSession.email)

        // PKCE 세션 삭제 (일회용)
        pkceSessionRepository.deleteByAuthorizationCode(authorizationCode)

        return OidcTokenResponse(
            accessToken = accessToken,
            tokenType = "Bearer",
            expiresIn = jwtExpiration / 1000,
            idToken = accessToken, // 간단히 access token을 id token으로도 사용
            scope = "openid profile email"
        )
    }

    /**
     * Access token으로 사용자 정보 조회
     */
    fun getUserInfo(accessToken: String): UserInfoResponse {
        val email = jwtTokenProvider.getEmail(accessToken)
        val authAccount = userServiceClient.findAuthAccountByEmail(email).data
            ?: throw IllegalArgumentException("User not found")

        return UserInfoResponse(
            sub = email,
            email = email,
            emailVerified = true,
            name = authAccount.user?.nickname,
            picture = null,
            provider = authAccount.provider
        )
    }

    private fun buildGoogleAuthUrl(state: String): String {
        return UriComponentsBuilder.fromHttpUrl(GOOGLE_AUTH_URL)
            .queryParam("client_id", googleClientId)
            .queryParam("redirect_uri", "$issuer/oauth2/callback/google")
            .queryParam("response_type", "code")
            .queryParam("scope", "openid email profile")
            .queryParam("state", state)
            .build()
            .toUriString()
    }

    private fun buildAppleAuthUrl(state: String): String {
        return UriComponentsBuilder.fromHttpUrl(APPLE_AUTH_URL)
            .queryParam("client_id", appleClientId)
            .queryParam("redirect_uri", "$issuer/oauth2/callback/apple")
            .queryParam("response_type", "code")
            .queryParam("scope", "openid email name")
            .queryParam("response_mode", "form_post")
            .queryParam("state", state)
            .build()
            .toUriString()
    }

    private fun exchangeProviderCodeForToken(provider: String, code: String): String {
        return when (provider.lowercase()) {
            "google" -> exchangeGoogleCode(code)
            "apple" -> exchangeAppleCode(code)
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }
    }

    private fun exchangeGoogleCode(code: String): String {
        val requestBody = mapOf(
            "code" to code,
            "client_id" to googleClientId,
            "client_secret" to googleClientSecret,
            "redirect_uri" to "$issuer/oauth2/callback/google",
            "grant_type" to "authorization_code"
        )

        val response = restTemplate.postForObject(
            GOOGLE_TOKEN_URL,
            requestBody,
            Map::class.java
        ) as? Map<*, *> ?: throw IllegalStateException("Failed to exchange Google code")

        return response["id_token"] as? String
            ?: throw IllegalStateException("Google token response missing id_token")
    }

    private fun exchangeAppleCode(code: String): String {
        if (!appleJwtService.isConfigured()) {
            throw IllegalStateException(
                "Apple OAuth2 is not configured. Please set apple.team-id, apple.key-id, and provide apple-private-key.p8"
            )
        }

        // Apple client_secret JWT 생성
        val clientSecret = appleJwtService.generateClientSecret()

        val requestBody = LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", appleClientId)
            add("client_secret", clientSecret)
            add("redirect_uri", "$issuer/oauth2/callback/apple")
            add("grant_type", "authorization_code")
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val request = HttpEntity(requestBody, headers)

        val response = restTemplate.postForObject(
            APPLE_TOKEN_URL,
            request,
            Map::class.java
        ) ?: throw IllegalStateException("Failed to exchange Apple code")

        return response["id_token"] as? String
            ?: throw IllegalStateException("Apple token response missing id_token")
    }

    /**
     * 보안 랜덤 state ID 생성 (32 bytes)
     */
    private fun generateSecureStateId(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    data class CallbackResult(
        val authorizationCode: String,
        val redirectUri: String,
        val clientState: String?
    )
}
