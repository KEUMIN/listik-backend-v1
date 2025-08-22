package com.listik.userservice.service

import com.listik.userservice.entity.AuthAccountEntity
import com.listik.userservice.entity.UserEntity

interface UserService {
    fun findByEmail(email: String): UserEntity?
    fun save(userEntity: UserEntity): UserEntity
    fun findAuthAccountByEmail(email: String): AuthAccountEntity?
    fun findAuthAccountByProvider(provider: String, providerUserId: String): AuthAccountEntity?
    fun saveAuthAccount(authAccountEntity: AuthAccountEntity): AuthAccountEntity
    fun createUserWithAuthAccount(nickname: String, email: String?, passwordHash: String?, provider: String?, providerUserId: String?): Pair<UserEntity, AuthAccountEntity>
}