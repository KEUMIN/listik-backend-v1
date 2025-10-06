package com.listik.authservice.pkce.service

import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

/**
 * PKCE 관련 유틸리티 서비스
 */
@Service
class PkceService {

    companion object {
        private const val CODE_VERIFIER_LENGTH = 64
        private const val AUTHORIZATION_CODE_LENGTH = 32
    }

    /**
     * PKCE code verifier 생성 (43-128자의 unreserved 문자열)
     */
    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(CODE_VERIFIER_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * code_verifier로부터 code_challenge 생성 (S256 방식)
     */
    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    /**
     * code_verifier와 code_challenge 검증
     */
    fun verifyCodeChallenge(codeVerifier: String, codeChallenge: String, method: String): Boolean {
        if (method != "S256") {
            return false
        }
        val computedChallenge = generateCodeChallenge(codeVerifier)
        return computedChallenge == codeChallenge
    }

    /**
     * Authorization code 생성
     */
    fun generateAuthorizationCode(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(AUTHORIZATION_CODE_LENGTH)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}
