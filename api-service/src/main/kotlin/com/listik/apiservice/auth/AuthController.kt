package com.listik.apiservice.auth

import com.listik.apiservice.auth.dto.request.IdTokenRequest
import com.listik.apiservice.auth.dto.request.SigninRequest
import com.listik.apiservice.auth.dto.request.SignupRequest
import com.listik.apiservice.auth.dto.response.TokenResponse
import com.listik.apiservice.common.dto.ApiResponse
import com.listik.authservice.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "회원가입, 로그인, OAuth2 관련 API")
class AuthController(
    private val authService: AuthService
) {
    @Operation(summary = "회원가입", description = "일반 회원가입")
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.signUp(request.email, request.password, request.name)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }
    @Operation(summary = "로그인", description = "이메일/비밀번호 로그인")
    @PostMapping("/signin")
    fun signin(@RequestBody request: SigninRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.signIn(request.email, request.password)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }
    @Operation(summary = "Google OAuth2 로그인", description = "구글 ID 토큰을 통해 로그인")
    @PostMapping("/oauth2/google")
    fun oauth2Google(@RequestBody request: IdTokenRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.authenticateGoogle(request.idToken)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }
    @Operation(summary = "Apple OAuth2 로그인", description = "애플 ID 토큰을 통해 로그인")
    @PostMapping("/oauth2/apple")
    fun oauth2Apple(@RequestBody request: IdTokenRequest): ResponseEntity<ApiResponse<TokenResponse>> {
        val token = authService.authenticateApple(request.idToken)
        return ResponseEntity.ok(ApiResponse.success(TokenResponse(token)))
    }
}
