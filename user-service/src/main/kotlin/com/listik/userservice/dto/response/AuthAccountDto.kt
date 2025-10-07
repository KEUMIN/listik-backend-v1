package com.listik.userservice.dto.response

import java.util.UUID

data class AuthAccountDto(
    val id: Long?,
    val provider: String,
    val providerUserId: String,
    val userId: UUID?
)