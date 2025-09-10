package com.listik.authservice.service

import com.listik.authservice.client.CreateUserWithAuthRequest
import com.listik.authservice.client.UserServiceClient
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.authservice.oauth.OAuth2AuthenticationManager
import com.listik.authservice.oauth.OAuth2ProviderType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userServiceClient: UserServiceClient,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val oauth2AuthenticationManager: OAuth2AuthenticationManager
) {
    
    fun signUp(email: String, password: String, name: String): String {
        val existingAccount = userServiceClient.findAuthAccountByEmail(email).data
        if (existingAccount != null) {
            throw IllegalStateException("Email already in use")
        }
        
        val request = CreateUserWithAuthRequest(
            nickname = name,
            email = email,
            passwordHash = passwordEncoder.encode(password),
            provider = null,
            providerUserId = null
        )
        
        userServiceClient.createUserWithAuthAccount(request)
        return jwtTokenProvider.createToken(email)
    }

    fun signIn(email: String, password: String): String {
        val authAccount = userServiceClient.findAuthAccountByEmail(email).data
            ?: throw IllegalArgumentException("User not found")
        
        if (authAccount.passwordHash == null || !passwordEncoder.matches(password, authAccount.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }
        
        return jwtTokenProvider.createToken(email)
    }

    fun authenticateGoogle(idTokenString: String): String {
        return oauth2AuthenticationManager.authenticate(OAuth2ProviderType.GOOGLE, idTokenString)
    }

    fun authenticateApple(idTokenString: String): String {
        return oauth2AuthenticationManager.authenticate(OAuth2ProviderType.APPLE, idTokenString)
    }
}