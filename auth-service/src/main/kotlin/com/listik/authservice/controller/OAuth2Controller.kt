package com.listik.authservice.controller

import com.listik.authservice.controller.dto.request.TokenRequest
import com.listik.authservice.controller.dto.response.OidcTokenResponse
import com.listik.authservice.controller.dto.response.UserInfoResponse
import com.listik.authservice.service.OidcService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2/OIDC API", description = "OAuth2 Authorization Code + PKCE 흐름")
class OAuth2Controller(
    private val oidcService: OidcService
) {

    @Operation(
        summary = "Authorization Endpoint",
        description = "OAuth2 인증 시작. provider의 OAuth2 인증 페이지로 리다이렉트"
    )
    @GetMapping("/authorize")
    fun authorize(
        @Parameter(description = "Response type (must be 'code')") @RequestParam(name = "response_type") responseType: String,
        @Parameter(description = "Client ID") @RequestParam(name = "client_id") clientId: String,
        @Parameter(description = "Redirect URI") @RequestParam(name = "redirect_uri") redirectUri: String,
        @Parameter(description = "CSRF protection state") @RequestParam(required = false) state: String?,
        @Parameter(description = "OAuth2 scopes") @RequestParam(required = false) scope: String?,
        @Parameter(description = "PKCE code challenge") @RequestParam(name = "code_challenge") codeChallenge: String,
        @Parameter(description = "PKCE code challenge method (S256)") @RequestParam(name = "code_challenge_method") codeChallengeMethod: String,
        @Parameter(description = "OAuth2 provider (google or apple)") @RequestParam provider: String,
        response: HttpServletResponse
    ) {
        if (responseType != "code") {
            throw IllegalArgumentException("Invalid response_type. Must be 'code'")
        }

        if (codeChallengeMethod != "S256") {
            throw IllegalArgumentException("Invalid code_challenge_method. Only S256 is supported")
        }

        val authorizationUrl = oidcService.buildAuthorizationUrl(
            provider = provider,
            redirectUri = redirectUri,
            state = state,
            codeChallenge = codeChallenge,
            codeChallengeMethod = codeChallengeMethod
        )

        response.sendRedirect(authorizationUrl)
    }

    @Operation(
        summary = "Google OAuth2 Callback",
        description = "Google로부터 authorization code를 받아 자체 authorization code 발급"
    )
    @GetMapping("/callback/google")
    fun callbackGoogle(
        @Parameter(description = "Authorization code from Google") @RequestParam code: String,
        @Parameter(description = "State parameter") @RequestParam(required = false) state: String?,
        @Parameter(description = "Error from provider") @RequestParam(required = false) error: String?,
        response: HttpServletResponse
    ) {
        if (error != null) {
            throw IllegalStateException("OAuth2 provider returned error: $error")
        }

        val callbackResult = oidcService.handleProviderCallback(
            provider = "google",
            providerCode = code,
            stateId = state
        )

        // 클라이언트 redirect_uri로 리다이렉트 (자체 authorization code 포함)
        val redirectUrl = buildRedirectUrl(
            callbackResult.redirectUri,
            callbackResult.authorizationCode,
            callbackResult.clientState
        )
        response.sendRedirect(redirectUrl)
    }

    @Operation(
        summary = "Apple OAuth2 Callback",
        description = "Apple로부터 authorization code를 받아 자체 authorization code 발급"
    )
    @GetMapping("/callback/apple")
    fun callbackApple(
        @Parameter(description = "Authorization code from Apple") @RequestParam code: String,
        @Parameter(description = "State parameter") @RequestParam(required = false) state: String?,
        @Parameter(description = "Error from provider") @RequestParam(required = false) error: String?,
        response: HttpServletResponse
    ) {
        if (error != null) {
            throw IllegalStateException("OAuth2 provider returned error: $error")
        }

        val callbackResult = oidcService.handleProviderCallback(
            provider = "apple",
            providerCode = code,
            stateId = state
        )

        val redirectUrl = buildRedirectUrl(
            callbackResult.redirectUri,
            callbackResult.authorizationCode,
            callbackResult.clientState
        )
        response.sendRedirect(redirectUrl)
    }

    @Operation(
        summary = "Token Endpoint",
        description = "Authorization code를 JWT 토큰으로 교환 (PKCE 검증 포함)"
    )
    @PostMapping("/token")
    fun token(@RequestBody request: TokenRequest): OidcTokenResponse {
        if (request.grantType != "authorization_code") {
            throw IllegalArgumentException("Invalid grant_type. Must be 'authorization_code'")
        }

        return oidcService.exchangeCodeForToken(
            authorizationCode = request.code,
            redirectUri = request.redirectUri,
            codeVerifier = request.codeVerifier
        )
    }

    @Operation(
        summary = "UserInfo Endpoint",
        description = "Access token으로 사용자 정보 조회"
    )
    @GetMapping("/userinfo")
    fun userInfo(
        @Parameter(description = "Bearer token") @RequestHeader("Authorization") authorization: String
    ): UserInfoResponse {
        val token = authorization.removePrefix("Bearer ").trim()
        return oidcService.getUserInfo(token)
    }

    private fun buildRedirectUrl(redirectUri: String, code: String, state: String?): String {
        val separator = if (redirectUri.contains("?")) "&" else "?"
        var url = "$redirectUri${separator}code=$code"
        if (state != null) {
            url += "&state=$state"
        }
        return url
    }
}
