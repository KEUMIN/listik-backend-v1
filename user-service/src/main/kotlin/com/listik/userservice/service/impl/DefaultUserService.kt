package com.listik.userservice.service.impl

import com.listik.userservice.entity.UserEntity
import com.listik.userservice.repository.UserRepository
import com.listik.userservice.service.UserService
import org.springframework.stereotype.Service

@Service
class DefaultUserService (
    private val userRepository: UserRepository
): UserService {
    override fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    override fun findByRefreshToken(refreshToken: String): UserEntity? {
        return userRepository.findByRefreshToken(refreshToken)
    }

    override fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }
}