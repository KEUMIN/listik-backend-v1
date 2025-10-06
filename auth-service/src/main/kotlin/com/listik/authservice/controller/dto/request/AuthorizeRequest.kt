package com.listik.authservice.controller.dto.request

/**
 * OAuth2 Authorization Request 파라미터
 */
data class AuthorizeRequest(
    val responseType: String, // "code"
    val clientId: String,
    val redirectUri: String,
    val state: String?,
    val scope: String?, // "openid profile email"
    val codeChallenge: String,
    val codeChallengeMethod: String, // "S256"
    val provider: String // "google" or "apple"
)
