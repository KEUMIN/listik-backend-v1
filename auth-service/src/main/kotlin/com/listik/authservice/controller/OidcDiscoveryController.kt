package com.listik.authservice.controller

import com.listik.authservice.controller.dto.response.OidcDiscoveryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "OIDC Discovery", description = "OpenID Connect Discovery Document")
class OidcDiscoveryController(
    @Value("\${spring.security.oauth2.authorizationserver.issuer}")
    private val issuer: String
) {

    @Operation(
        summary = "OIDC Discovery Document",
        description = "OpenID Connect Discovery metadata endpoint"
    )
    @GetMapping("/.well-known/openid-configuration")
    fun getDiscoveryDocument(): OidcDiscoveryResponse {
        return OidcDiscoveryResponse(
            issuer = issuer,
            authorizationEndpoint = "$issuer/oauth2/authorize",
            tokenEndpoint = "$issuer/oauth2/token",
            userinfoEndpoint = "$issuer/oauth2/userinfo",
            responseTypesSupported = listOf("code"),
            subjectTypesSupported = listOf("public"),
            idTokenSigningAlgValuesSupported = listOf("HS256"),
            scopesSupported = listOf("openid", "profile", "email"),
            tokenEndpointAuthMethodsSupported = listOf("client_secret_post", "client_secret_basic", "none"),
            claimsSupported = listOf("sub", "email", "email_verified", "name", "picture", "provider"),
            codeChallengeMethodsSupported = listOf("S256")
        )
    }
}
