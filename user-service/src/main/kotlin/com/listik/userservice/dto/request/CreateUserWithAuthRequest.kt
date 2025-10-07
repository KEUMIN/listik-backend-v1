package com.listik.userservice.dto.request

data class CreateUserWithAuthRequest(
    val provider: String,
    val providerUserId: String
)