package com.listik.authservice.oauth.strategy

import com.listik.authservice.oauth.OAuth2ProviderType
import com.listik.authservice.oauth.model.OAuth2UserInfo

/**
 * OAuth2 인증 제공자별 전략 인터페이스
 * Strategy Pattern을 사용하여 각 OAuth2 제공자(Google, Apple)별 토큰 검증 로직을 캡슐화
 */
interface OAuth2AuthenticationStrategy {

    /**
     * 지원하는 OAuth2 제공자 타입
     */
    val providerType: OAuth2ProviderType

    /**
     * ID 토큰을 검증하고 사용자 정보를 추출
     *
     * @param idToken OAuth2 제공자로부터 받은 ID 토큰
     * @return 검증된 OAuth2 사용자 정보
     * @throws OAuth2TokenValidationException 토큰이 유효하지 않거나 검증 실패시
     */
    fun validateAndExtractUserInfo(idToken: String): OAuth2UserInfo
}