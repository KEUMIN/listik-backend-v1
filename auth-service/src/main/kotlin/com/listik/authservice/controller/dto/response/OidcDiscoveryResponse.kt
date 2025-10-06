package com.listik.authservice.controller.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OIDC Discovery Document
 * https://openid.net/specs/openid-connect-discovery-1_0.html
 */
data class OidcDiscoveryResponse(
    val issuer: String,

    @JsonProperty("authorization_endpoint")
    val authorizationEndpoint: String,

    @JsonProperty("token_endpoint")
    val tokenEndpoint: String,

    @JsonProperty("userinfo_endpoint")
    val userinfoEndpoint: String,

    @JsonProperty("jwks_uri")
    val jwksUri: String? = null,

    @JsonProperty("response_types_supported")
    val responseTypesSupported: List<String> = listOf("code"),

    @JsonProperty("subject_types_supported")
    val subjectTypesSupported: List<String> = listOf("public"),

    @JsonProperty("id_token_signing_alg_values_supported")
    val idTokenSigningAlgValuesSupported: List<String> = listOf("HS256"),

    @JsonProperty("scopes_supported")
    val scopesSupported: List<String> = listOf("openid", "profile", "email"),

    @JsonProperty("token_endpoint_auth_methods_supported")
    val tokenEndpointAuthMethodsSupported: List<String> = listOf("client_secret_post", "client_secret_basic"),

    @JsonProperty("claims_supported")
    val claimsSupported: List<String> = listOf("sub", "email", "email_verified", "name", "picture", "provider"),

    @JsonProperty("code_challenge_methods_supported")
    val codeChallengeMethodsSupported: List<String> = listOf("S256")
)
