package com.listik.apiservice.controller

import com.listik.apiservice.dto.IdTokenRequest
import com.listik.apiservice.dto.SigninRequest
import com.listik.apiservice.dto.SignupRequest
import com.listik.apiservice.dto.TokenResponse
import com.listik.authservice.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<TokenResponse> {
        val token = authService.signUp(request.email, request.password, request.name)
        return ResponseEntity.ok(TokenResponse(token))
    }

    @PostMapping("/signin")
    fun signin(@RequestBody request: SigninRequest): ResponseEntity<TokenResponse> {
        val token = authService.signIn(request.email, request.password)
        return ResponseEntity.ok(TokenResponse(token))
    }

    @PostMapping("/oauth2/google")
    fun oauth2Google(@RequestBody request: IdTokenRequest): ResponseEntity<TokenResponse> {
        val token = authService.authenticateGoogle(request.idToken)
        return ResponseEntity.ok(TokenResponse(token))
    }

}
