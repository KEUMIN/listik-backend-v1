package com.listik.userservice.dto.response

data class CreateUserWithAuthResponse(
    val userId: Long,
    val authAccountId: Long
)