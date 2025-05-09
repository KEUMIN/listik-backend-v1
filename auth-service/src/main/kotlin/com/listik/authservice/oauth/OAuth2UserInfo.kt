package com.listik.authservice.oauth

import com.listik.coreservice.dto.UserProfile
import org.springframework.security.oauth2.core.user.OAuth2User

interface OAuth2UserInfo {
    fun toUserProfile(oAuth2User: OAuth2User): UserProfile
}