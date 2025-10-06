package com.listik.authservice.controller.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OIDC Token Response
 */
data class OidcTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String = "Bearer",

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @JsonProperty("id_token")
    val idToken: String? = null,

    @JsonProperty("scope")
    val scope: String? = null
)
