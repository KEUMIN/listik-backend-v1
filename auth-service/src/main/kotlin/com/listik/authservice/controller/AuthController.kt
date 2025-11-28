package com.listik.authservice.controller

import com.listik.authservice.controller.dto.request.RefreshTokenRequest
import com.listik.authservice.controller.dto.request.VerifyIdTokenRequest
import com.listik.authservice.controller.dto.response.AuthResponse
import com.listik.authservice.service.AuthService
import com.listik.coreservice.dto.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "App Authentication API", description = "모바일 앱용 인증 API")
class AuthController(
    private val authService: AuthService
) {
    private val log = LoggerFactory.getLogger(AuthController::class.java)

    @Operation(
        summary = "ID Token 검증 및 백엔드 토큰 발급",
        description = """
            앱이 Google/Apple로부터 받은 ID Token을 검증하고 백엔드 JWT 토큰을 발급합니다.

            흐름:
            1. 앱이 OIDC Provider와 직접 통신 (PKCE 사용)
            2. 앱이 ID Token을 백엔드로 전송
            3. 백엔드가 ID Token 검증 및 사용자 조회/생성
            4. 백엔드 Access Token과 Refresh Token 발급
        """
    )
    @PostMapping("/verify")
    fun verifyIdToken(
        @RequestBody request: VerifyIdTokenRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.verifyIdTokenAndIssueTokens(request.idToken)
        return ResponseEntity.ok(ApiResponse.success(authResponse))
    }

    @Operation(
        summary = "백엔드 토큰 갱신",
        description = """
            백엔드 Refresh Token으로 새로운 Access Token과 Refresh Token을 발급합니다.

            주의:
            - Refresh Token은 일회용입니다. 사용 후 새로운 Refresh Token이 발급됩니다.
            - 단일 디바이스 정책: 새로운 로그인 시 이전 Refresh Token은 무효화됩니다.
        """
    )
    @PostMapping("/refresh")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.refreshTokens(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success(authResponse))
    }

    @Operation(
        summary = "로그아웃",
        description = """
            로그아웃을 수행합니다.

            처리 사항:
            - Refresh Token 삭제
            - Access Token을 Redis 블랙리스트에 저장 (유효기간만큼)
        """
    )
    @PostMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<ApiResponse<Unit>> {
        val accessToken = authorizationHeader.removePrefix("Bearer ").trim()
        authService.logout(accessToken)
        return ResponseEntity.ok(ApiResponse.success(Unit))
    }

    @GetMapping("/apple/callback")
    fun appleWebOAuthCallback(
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) state: String?,
        @RequestParam(required = false) error: String?,
        @RequestParam("id_token", required = false) idToken: String?,
        response: HttpServletResponse,
    ) {
        log.info("Apple callback received - code: ${code != null}, error: $error, idToken: ${idToken != null}")

        // 에러 처리
        if (error != null) {
            log.error("Apple auth error: $error")
            response.contentType = "text/html"
            response.writer.write(
                """
            <html>
            <body>
                <h1>로그인 실패</h1>
                <p>에러: $error</p>
                <p>이 창을 닫아주세요.</p>
                <script>
                    setTimeout(function() { window.close(); }, 3000);
                </script>
            </body>
            </html>
        """.trimIndent()
            )
            return
        }

        // code가 있으면 그대로 HTML로 반환 (sign_in_with_apple이 파싱)
        if (code != null) {
            log.info("Returning code to client")
            response.contentType = "text/html"
            response.writer.write(
                """
            <!DOCTYPE html>
            <html>
            <head><title>Sign in with Apple</title></head>
            <body>
                <script>
                    // sign_in_with_apple 패키지가 이 정보를 파싱합니다
                    const urlParams = new URLSearchParams(window.location.search);
                    const code = urlParams.get('code');
                    const state = urlParams.get('state');
                    
                    // 디버깅용
                    console.log('Code:', code);
                    console.log('State:', state);
                    
                    // 자동으로 닫힘 (sign_in_with_apple이 감지)
                </script>
                <p>로그인 처리 중...</p>
            </body>
            </html>
        """.trimIndent()
            )
            return
        }

        // code도 없고 error도 없으면
        log.warn("No code or error in Apple callback")
        response.contentType = "text/html"
        response.writer.write(
            """
        <html>
        <body>
            <h1>알 수 없는 응답</h1>
            <p>이 창을 닫아주세요.</p>
        </body>
        </html>
    """.trimIndent()
        )
    }
}
