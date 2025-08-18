package com.listik.userservice.service


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.listik.userservice.jwt.JwtTokenProvider
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
        if (userService.findAuthAccountByEmail(email) != null) throw IllegalStateException("Email already in use")
        val (user, authAccount) = userService.createUserWithAuthAccount(
            nickname = name,
            email = email,
            passwordHash = passwordEncoder.encode(password),
            provider = null,
            providerUserId = null
        )
        return jwtTokenProvider.createToken(email)
    }

    fun signIn(email: String, password: String): String {
        val authAccount = userService.findAuthAccountByEmail(email) ?: throw IllegalArgumentException("User not found")
        if (authAccount.passwordHash == null || !passwordEncoder.matches(password, authAccount.passwordHash!!)) {
            throw IllegalArgumentException("Invalid credentials")
        }
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

        val existingAuthAccount = userService.findAuthAccountByProvider("GOOGLE", providerId)
        if (existingAuthAccount != null) {
            return jwtTokenProvider.createToken(existingAuthAccount.email!!)
        }

        val emailAuthAccount = userService.findAuthAccountByEmail(email)
        if (emailAuthAccount != null) {
            if (emailAuthAccount.provider == null) {
                emailAuthAccount.provider = "GOOGLE"
                emailAuthAccount.providerUserId = providerId
                userService.saveAuthAccount(emailAuthAccount)
            }
            return jwtTokenProvider.createToken(email)
        }

        val (user, authAccount) = userService.createUserWithAuthAccount(
            nickname = name,
            email = email,
            passwordHash = null,
            provider = "GOOGLE",
            providerUserId = providerId
        )

        return jwtTokenProvider.createToken(authAccount.email!!)
    }

    fun authenticateApple(idTokenString: String): String {
        val jwkSet = JWKSet.load(URL("https://appleid.apple.com/auth/keys"))
        val jwkSource: JWKSource<SecurityContext> = ImmutableJWKSet(jwkSet)

        val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
        val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)
        jwtProcessor.jwsKeySelector = keySelector

        val claims: JWTClaimsSet = jwtProcessor.process(idTokenString, null)

        // 🔐 보안 필수 검증
        require(claims.issuer == "https://appleid.apple.com") { "Invalid issuer: ${claims.issuer}" }
        require(claims.audience.contains(appleClientId)) { "Invalid audience: ${claims.audience}" }

        val providerId = claims.subject  // Apple 고유 사용자 ID (변하지 않음)
        val email = claims.getStringClaim("email") ?: "user-$providerId@apple.local" // 첫 로그인 외 null 가능

        val existingAuthAccount = userService.findAuthAccountByProvider("APPLE", providerId)
        if (existingAuthAccount != null) {
            return jwtTokenProvider.createToken(existingAuthAccount.email!!)
        }

        val emailAuthAccount = userService.findAuthAccountByEmail(email)
        if (emailAuthAccount != null) {
            if (emailAuthAccount.provider == null) {
                emailAuthAccount.provider = "APPLE"
                emailAuthAccount.providerUserId = providerId
                userService.saveAuthAccount(emailAuthAccount)
            }
            return jwtTokenProvider.createToken(email)
        }

        val (user, authAccount) = userService.createUserWithAuthAccount(
            nickname = "",
            email = email,
            passwordHash = null,
            provider = "APPLE",
            providerUserId = providerId
        )

        return jwtTokenProvider.createToken(authAccount.email!!)
    }

}