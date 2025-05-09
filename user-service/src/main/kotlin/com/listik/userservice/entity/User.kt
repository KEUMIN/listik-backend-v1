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