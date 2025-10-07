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
                handleExistingOAuthAccount(accountStatus.authAccount, userInfo)

            is AccountStatus.NewAccount -> handleNewAccount(userInfo)
        }

    fun determineAccountStatus(userInfo: OAuth2UserInfo): AccountStatus {
        val existingOAuthAccount = userServiceClient
            .findAuthAccountByProvider(userInfo.provider.displayName, userInfo.providerId)
            .data

        if (existingOAuthAccount != null) {
            return AccountStatus.ExistingOAuthAccount(existingOAuthAccount)
        }

        return AccountStatus.NewAccount
    }

    private fun handleExistingOAuthAccount(authAccount: AuthAccountDto, userInfo: OAuth2UserInfo): String {
        requireNotNull(authAccount.userId)
        // JWT에 userId(UUID)를 저장
        return jwtTokenProvider.createToken(authAccount.userId.toString())
    }

    private fun handleNewAccount(userInfo: OAuth2UserInfo): String {
        val request = CreateUserWithAuthRequest(
            provider = userInfo.provider.displayName,
            providerUserId = userInfo.providerId
        )

        val response = userServiceClient.createUserWithAuthAccount(request)

        // JWT에 userId(UUID)를 저장
        return jwtTokenProvider.createToken(response.data!!.userId.toString())
    }
}