package com.listik.userservice.service.impl

import com.listik.userservice.entity.User
import com.listik.userservice.repository.UserRepository
import com.listik.userservice.service.UserService
import org.springframework.stereotype.Service

@Service
class DefaultUserService (
    private val userRepository: UserRepository
): UserService {
    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun findByRefreshToken(refreshToken: String): User? {
        return userRepository.findByRefreshToken(refreshToken)
    }

    override fun save(user: User): User {
        return userRepository.save(user)
    }
}