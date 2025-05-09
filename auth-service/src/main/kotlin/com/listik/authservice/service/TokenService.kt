package com.listik.authservice.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService {
    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val issuer = "example.com"

    fun createAccessToken(email: String): String = Jwts.builder()
        .setSubject(email)
        .setIssuer(issuer)
        .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 15))
        .signWith(secretKey)
        .compact()

    fun createRefreshToken(): String = Jwts.builder()
        .setIssuer(issuer)
        .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))
        .signWith(secretKey)
        .compact()

    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
        true
    } catch (e: Exception) {
        false
    }
}