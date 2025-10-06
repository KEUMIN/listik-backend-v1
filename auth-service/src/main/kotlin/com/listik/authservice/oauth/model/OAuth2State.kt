package com.listik.authservice.oauth.model

import java.io.Serializable

/**
 * OAuth2 authorization 요청 시 상태 정보
 * Redis에 저장되어 CSRF 공격 방지 및 파라미터 전달
 */
data class OAuth2State(
    val stateId: String, // 랜덤 생성된 state ID
    val clientState: String?, // 클라이언트가 전달한 원본 state
    val redirectUri: String,
    val codeChallenge: String,
    val codeChallengeMethod: String,
    val provider: String,
    val createdAt: Long
) : Serializable
