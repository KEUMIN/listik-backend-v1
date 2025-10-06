package com.listik.authservice.pkce.model

import java.io.Serializable

/**
 * PKCE 세션 정보를 Redis에 저장하기 위한 모델
 *
 * @property authorizationCode 발급된 authorization code
 * @property codeChallenge PKCE code challenge
 * @property codeChallengeMethod code challenge 생성 방법 (S256)
 * @property redirectUri 클라이언트 redirect URI
 * @property state CSRF 방지를 위한 state 파라미터
 * @property email 인증된 사용자 이메일
 * @property provider OAuth2 제공자 (google, apple)
 * @property createdAt 생성 시각 (밀리초)
 */
data class PkceSession(
    val authorizationCode: String,
    val codeChallenge: String,
    val codeChallengeMethod: String,
    val redirectUri: String,
    val state: String?,
    val email: String,
    val provider: String,
    val createdAt: Long
) : Serializable
