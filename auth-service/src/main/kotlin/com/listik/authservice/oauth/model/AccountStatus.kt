package com.listik.authservice.oauth.model

import com.listik.authservice.client.AuthAccountDto

sealed class AccountStatus {

    data class ExistingOAuthAccount(
        val authAccount: AuthAccountDto
    ) : AccountStatus()

    data object NewAccount : AccountStatus()
}