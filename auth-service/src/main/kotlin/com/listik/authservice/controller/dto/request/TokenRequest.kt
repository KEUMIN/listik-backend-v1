package com.listik.authservice.controller.dto.request

/**
 * OAuth2 Token Request 파라미터
 */
data class TokenRequest(
    val grantType: String, // "authorization_code"
    val code: String,
    val redirectUri: String,
    val clientId: String,
    val codeVerifier: String
)
