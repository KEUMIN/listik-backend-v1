package com.listik.userservice.repository

import com.listik.userservice.entity.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByRefreshToken(refreshToken: String): User?
}