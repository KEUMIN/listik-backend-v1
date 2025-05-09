package com.listik.authservice.service

import com.listik.authservice.oauth.provider.GoogleUserInfo
import com.listik.userservice.entity.User
import com.listik.userservice.service.UserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userService: UserService,
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = userRequest.clientRegistration.registrationId

        val profile = when (provider) {
            "google" -> GoogleUserInfo().toUserProfile(oAuth2User)
            else -> throw IllegalArgumentException("Unsupported provider: $provider")
        }

        val user = userService.findByEmail(profile.email)
            ?.apply {
                this.provider = profile.provider
                this.providerId = profile.providerId
            }
            ?: User(
                email = profile.email,
                name = profile.name,
                provider = profile.provider,
                providerId = profile.providerId
            )

        userService.save(user)
        return oAuth2User
    }
}