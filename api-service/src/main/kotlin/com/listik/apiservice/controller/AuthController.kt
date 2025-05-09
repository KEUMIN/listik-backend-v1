package com.listik.apiservice.controller

import com.listik.authservice.service.TokenService
import com.listik.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val tokenService: TokenService
) {
    data class TokenRequest(val email: String)
    data class TokenResponse(val accessToken: String, val refreshToken: String)

    @PostMapping("/token")
    fun issueToken(@RequestBody request: TokenRequest): ResponseEntity<TokenResponse> {
        val user = userService.findByEmail(request.email)
            ?: return ResponseEntity.notFound().build()

        val access = tokenService.createAccessToken(user.email)
        val refresh = tokenService.createRefreshToken()
        user.refreshToken = refresh
        userService.save(user)

        return ResponseEntity.ok(TokenResponse(access, refresh))
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody refreshToken: String): ResponseEntity<TokenResponse> {
        val user = userService.findByRefreshToken(refreshToken)
            ?: return ResponseEntity.status(401).build()

        if (!tokenService.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build()
        }

        val newAccess = tokenService.createAccessToken(user.email)
        return ResponseEntity.ok(TokenResponse(newAccess, refreshToken))
    }
}
