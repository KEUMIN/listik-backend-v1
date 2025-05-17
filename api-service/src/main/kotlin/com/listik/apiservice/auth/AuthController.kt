package com.listik.apiservice.auth

import com.listik.apiservice.auth.dto.request.IdTokenRequest
import com.listik.apiservice.auth.dto.request.SigninRequest
import com.listik.apiservice.auth.dto.request.SignupRequest
import com.listik.apiservice.auth.dto.response.TokenResponse
import com.listik.apiservice.common.dto.ApiResponse
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
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.signUp(request.email, request.password, request.name)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }

    @PostMapping("/signin")
    fun signin(@RequestBody request: SigninRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.signIn(request.email, request.password)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }

    @PostMapping("/oauth2/google")
    fun oauth2Google(@RequestBody request: IdTokenRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.authenticateGoogle(request.idToken)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }

    @PostMapping("/oauth2/apple")
    fun oauth2Apple(@RequestBody request: IdTokenRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.authenticateApple(request.idToken)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }
}
