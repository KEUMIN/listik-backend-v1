package com.listik.authservice.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 갱신 요청")
data class RefreshTokenRequest(
    @Schema(description = "백엔드에서 발급한 Refresh Token", required = true)
    val refreshToken: String
)
