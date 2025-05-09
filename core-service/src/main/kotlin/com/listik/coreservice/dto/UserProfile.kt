package com.listik.coreservice.dto

data class UserProfile(
    val email: String,
    val name: String,
    val provider: String,
    val providerId: String
)
