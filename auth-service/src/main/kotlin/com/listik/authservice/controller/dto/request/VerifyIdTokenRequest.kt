package com.listik.authservice.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "ID Token 검증 요청")
data class VerifyIdTokenRequest(
    @Schema(description = "OIDC Provider로부터 받은 ID Token", required = true)
    val idToken: String
)
