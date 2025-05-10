package com.listik.userservice.repository

import com.listik.userservice.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByRefreshToken(refreshToken: String): User?
}