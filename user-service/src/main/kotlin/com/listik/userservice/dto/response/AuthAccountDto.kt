package com.listik.userservice.dto.response

data class AuthAccountDto(
    val id: Long?,
    val email: String?,
    val passwordHash: String?,
    val provider: String?,
    val providerUserId: String?,
    val userId: Long?
)