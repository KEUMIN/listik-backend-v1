package com.listik.authservice.oauth2.provider

import com.listik.authservice.oauth2.OAuth2UserInfo
import com.listik.coreservice.dto.UserProfile
import org.springframework.security.oauth2.core.user.OAuth2User

class GoogleUserInfo : OAuth2UserInfo {
    override fun toUserProfile(oAuth2User: OAuth2User): UserProfile {
        val email = oAuth2User.getAttribute<String>("email") ?: ""
        val name = oAuth2User.getAttribute<String>("name") ?: ""
        val id = oAuth2User.getAttribute<String>("sub") ?: ""
        return UserProfile(email, name, "google", id)
    }
}