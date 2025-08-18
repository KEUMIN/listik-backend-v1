package com.listik.userservice.service

import com.listik.userservice.entity.UserEntity

interface UserService {
    fun findByEmail(email: String): UserEntity?
    fun findByRefreshToken(refreshToken: String): UserEntity?
    fun save(userEntity: UserEntity): UserEntity
}