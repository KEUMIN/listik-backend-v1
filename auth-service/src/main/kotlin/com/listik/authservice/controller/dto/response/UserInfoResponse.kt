package com.listik.authservice.controller.dto.response

/**
 * OIDC UserInfo Response
 */
data class UserInfoResponse(
    val sub: String, // subject (user identifier)
    val email: String?,
    val emailVerified: Boolean? = null,
    val name: String?,
    val picture: String?,
    val provider: String?
)
