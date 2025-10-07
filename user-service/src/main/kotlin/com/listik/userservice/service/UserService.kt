package com.listik.userservice.service

import com.listik.userservice.entity.AuthAccountEntity
import com.listik.userservice.entity.UserEntity

interface UserService {
    fun save(userEntity: UserEntity): UserEntity
    fun findAuthAccountByProvider(provider: String, providerUserId: String): AuthAccountEntity?
    fun saveAuthAccount(authAccountEntity: AuthAccountEntity): AuthAccountEntity
    fun createUserWithAuthAccount(provider: String, providerUserId: String): Pair<UserEntity, AuthAccountEntity>
}