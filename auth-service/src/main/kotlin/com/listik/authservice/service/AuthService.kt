package com.listik.authservice.service


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.userservice.entity.User
import com.listik.userservice.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") private val googleClientId: String
) {
    fun signUp(email: String, password: String, name: String): String {
        if (userService.findByEmail(email) != null) throw IllegalStateException("Email already in use")
        val user = User(email = email, name = name, passwordHash = passwordEncoder.encode(password), provider = null, providerId = null)
        userService.save(user)
        return jwtTokenProvider.createToken(email)
    }

    fun signIn(email: String, password: String): String {
        val user = userService.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        if (!passwordEncoder.matches(password, user.passwordHash)) throw IllegalArgumentException("Invalid credentials")
        return jwtTokenProvider.createToken(email)
    }

    fun authenticateGoogle(idTokenString: String): String {
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(listOf(googleClientId))
            .build()

        val idToken: GoogleIdToken = verifier.verify(idTokenString)
            ?: throw IllegalArgumentException("Invalid ID token")

        val payload = idToken.payload
        val email = payload.email
        val name = payload["name"] as? String ?: "GoogleUser"
        val providerId = payload.subject // Google 고유 ID

        val user = userService.findByEmail(email)?.apply {
            if (this.provider.isNullOrBlank() || this.providerId.isNullOrBlank()) {
                this.provider = "google"
                this.providerId = providerId
                userService.save(this)
            }
        } ?: userService.save(
            User(email = email, name = name, provider = "google", providerId = providerId)
        )

        return jwtTokenProvider.createToken(user.email)
    }

}
