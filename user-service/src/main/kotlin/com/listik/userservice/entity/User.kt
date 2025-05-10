package com.listik.userservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var email: String,
    var name: String,
    var provider: String?,
    var providerId: String?,
    var passwordHash: String? = null,
    var refreshToken: String? = null
)
{
    protected constructor() : this(
        id      = 0,
        email   = "",
        name    = "",
        provider    = null,
        providerId  = null,
        passwordHash = null,
        refreshToken = null
    )
}