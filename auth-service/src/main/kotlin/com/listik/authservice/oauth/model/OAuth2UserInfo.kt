package com.listik.authservice.oauth.model

import com.listik.authservice.oauth.OAuth2ProviderType

data class OAuth2UserInfo(
    val provider: OAuth2ProviderType,
    val providerId: String,
    val email: String,
    val name: String?
)