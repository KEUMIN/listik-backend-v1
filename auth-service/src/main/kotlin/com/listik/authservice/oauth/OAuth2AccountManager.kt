package com.listik.authservice.oauth

import com.listik.authservice.client.AuthAccountDto
import com.listik.authservice.client.CreateUserWithAuthRequest
import com.listik.authservice.client.UserServiceClient
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.authservice.oauth.model.AccountStatus
import com.listik.authservice.oauth.model.OAuth2UserInfo
import org.springframework.stereotype.Component

@Component
class OAuth2AccountManager(
    private val userServiceClient: UserServiceClient,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun processAuthentication(userInfo: OAuth2UserInfo): String =
        when (val accountStatus = determineAccountStatus(userInfo)) {
            is AccountStatus.ExistingOAuthAccount ->
                handleExistingOAuthAccount(accountStatus.authAccount)

            is AccountStatus.ExistingEmailAccount ->
                handleExistingEmailAccount(accountStatus.authAccount, userInfo)

            is AccountStatus.NewAccount -> handleNewAccount(userInfo)
        }

    fun determineAccountStatus(userInfo: OAuth2UserInfo): AccountStatus {
        val existingOAuthAccount = userServiceClient
            .findAuthAccountByProvider(userInfo.provider.displayName, userInfo.providerId)
            .data

        if (existingOAuthAccount != null) {
            return AccountStatus.ExistingOAuthAccount(existingOAuthAccount)
        }

        val existingEmailAccount = userServiceClient
            .findAuthAccountByEmail(userInfo.email)
            .data

        if (existingEmailAccount != null) {
            return AccountStatus.ExistingEmailAccount(existingEmailAccount)
        }

        return AccountStatus.NewAccount
    }

    private fun handleExistingOAuthAccount(authAccount: AuthAccountDto): String {
        requireNotNull(authAccount.email)
        return jwtTokenProvider.createToken(authAccount.email)
    }

    private fun handleExistingEmailAccount(
        authAccount: AuthAccountDto,
        userInfo: OAuth2UserInfo
    ): String {
        requireNotNull(authAccount.id)
        requireNotNull(authAccount.email)

        if (authAccount.provider == null) {
            val updatedAccount = authAccount.copy(
                provider = userInfo.provider.displayName,
                providerUserId = userInfo.providerId
            )
            userServiceClient.updateAuthAccount(authAccount.id, updatedAccount)
        }

        return jwtTokenProvider.createToken(authAccount.email)
    }

    private fun handleNewAccount(userInfo: OAuth2UserInfo): String {
        val request = CreateUserWithAuthRequest(
            nickname = userInfo.name ?: generateDefaultNickname(userInfo),
            email = userInfo.email,
            passwordHash = null,
            provider = userInfo.provider.displayName,
            providerUserId = userInfo.providerId
        )

        userServiceClient.createUserWithAuthAccount(request)

        return jwtTokenProvider.createToken(userInfo.email)
    }

    private fun generateDefaultNickname(userInfo: OAuth2UserInfo): String =
        when (userInfo.provider) {
            OAuth2ProviderType.GOOGLE -> userInfo.name ?: "User"
            OAuth2ProviderType.APPLE -> "User"
        }
}