package com.listik.userservice.service

import com.listik.userservice.entity.User

interface UserService {
    fun findByEmail(email: String): User?
    fun findByRefreshToken(refreshToken: String): User?
    fun save(user: User): User
}