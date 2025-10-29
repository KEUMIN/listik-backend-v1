package com.listik.authservice.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "인증 응답")
data class AuthResponse(
    @Schema(description = "백엔드 Access Token (JWT)", required = true)
    val accessToken: String,

    @Schema(description = "백엔드 Refresh Token", required = true)
    val refreshToken: String,

    @Schema(description = "사용자 정보", required = true)
    val user: UserDto
)

@Schema(description = "사용자 정보")
data class UserDto(
    @Schema(description = "사용자 ID (UUID)", required = true)
    val id: UUID,

    @Schema(description = "OAuth2 Provider", required = true, example = "GOOGLE")
    val provider: String,

    @Schema(description = "Provider User ID", required = true)
    val providerUserId: String
)
