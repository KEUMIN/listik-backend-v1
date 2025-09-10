package com.listik.authservice.oauth.exception

/**
 * OAuth2 토큰 검증 실패 시 발생하는 예외
 */
class OAuth2TokenValidationException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)