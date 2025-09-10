package com.listik.authservice.oauth

import com.listik.authservice.oauth.strategy.OAuth2AuthenticationStrategy
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationManager(
    private val authStrategies: List<OAuth2AuthenticationStrategy>,
    private val accountManager: OAuth2AccountManager
) {

    private val strategyMap: Map<OAuth2ProviderType, OAuth2AuthenticationStrategy> by lazy {
        authStrategies.associateBy { it.providerType }
    }

    /**
     * 지정된 OAuth2 제공자로 인증 처리
     *
     * @param providerType OAuth2 제공자 타입
     * @param idToken OAuth2 제공자로부터 받은 ID 토큰
     * @return 인증 성공 시 JWT 토큰
     * @throws IllegalArgumentException 지원하지 않는 제공자인 경우
     * @throws OAuth2TokenValidationException 토큰 검증 실패시
     */
    fun authenticate(providerType: OAuth2ProviderType, idToken: String): String {
        val strategy = strategyMap[providerType]
            ?: throw IllegalArgumentException("Unsupported OAuth2 provider: $providerType")

        val userInfo = strategy.validateAndExtractUserInfo(idToken)

        return accountManager.processAuthentication(userInfo)
    }
}