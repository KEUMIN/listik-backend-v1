package com.listik.authservice.service


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.listik.authservice.jwt.JwtTokenProvider
import com.listik.userservice.entity.User
import com.listik.userservice.service.UserService
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.net.URL

@Service
class AuthService(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private val googleClientId: String,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val appleClientId: String
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

    fun authenticateApple(idTokenString: String): String {
        // 1. Apple 공개키 JWKSet 직접 가져오기
        val jwkSet = JWKSet.load(URL("https://appleid.apple.com/auth/keys"))
        val jwkSource: JWKSource<SecurityContext> = ImmutableJWKSet(jwkSet)

        // 2. JWT Processor 구성
        val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
        val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)
        jwtProcessor.setJWSKeySelector(keySelector)

        // 3. JWT 토큰 파싱 & 검증
        val claims: JWTClaimsSet = jwtProcessor.process(idTokenString, null)

        // 4. 클레임 검증
        if (claims.issuer != "https://appleid.apple.com") {
            throw IllegalArgumentException("Invalid issuer: ${claims.issuer}")
        }
        if (!claims.audience.contains(appleClientId)) {
            throw IllegalArgumentException("Invalid audience: ${claims.audience}")
        }

        val email = claims.getStringClaim("email") ?: throw IllegalArgumentException("No email in token")
        val providerId = claims.subject

        // 5. 사용자 처리
        val user = userService.findByEmail(email)?.apply {
            if (this.provider.isNullOrBlank() || this.providerId.isNullOrBlank()) {
                this.provider = "apple"
                this.providerId = providerId
                userService.save(this)
            }
        } ?: userService.save(
            User(email = email, name = email.substringBefore("@"), provider = "apple", providerId = providerId)
        )

        // 6. JWT 생성
        return jwtTokenProvider.createToken(user.email)
    }
}
