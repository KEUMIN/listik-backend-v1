package com.listik.userservice.dto.response

import java.util.UUID

data class CreateUserWithAuthResponse(
    val userId: UUID,
    val authAccountId: Long
)