package com.listik.userservice.service.impl

import com.listik.userservice.entity.AuthAccountEntity
import com.listik.userservice.entity.UserEntity
import com.listik.userservice.repository.AuthAccountRepository
import com.listik.userservice.repository.UserRepository
import com.listik.userservice.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultUserService (
    private val userRepository: UserRepository,
    private val authAccountRepository: AuthAccountRepository
): UserService {
    override fun findByEmail(email: String): UserEntity? {
        return authAccountRepository.findByEmail(email)?.user
    }

    override fun findByRefreshToken(refreshToken: String): UserEntity? {
        return userRepository.findByRefreshToken(refreshToken)
    }

    override fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    override fun findAuthAccountByEmail(email: String): AuthAccountEntity? {
        return authAccountRepository.findByEmail(email)
    }

    override fun findAuthAccountByProvider(provider: String, providerUserId: String): AuthAccountEntity? {
        return authAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
    }

    override fun saveAuthAccount(authAccountEntity: AuthAccountEntity): AuthAccountEntity {
        return authAccountRepository.save(authAccountEntity)
    }

    @Transactional
    override fun createUserWithAuthAccount(
        nickname: String, 
        email: String?, 
        passwordHash: String?, 
        provider: String?, 
        providerUserId: String?
    ): Pair<UserEntity, AuthAccountEntity> {
        val user = userRepository.save(UserEntity(nickname = nickname))
        val authAccount = authAccountRepository.save(
            AuthAccountEntity(
                user = user,
                email = email,
                passwordHash = passwordHash,
                provider = provider,
                providerUserId = providerUserId
            )
        )
        return Pair(user, authAccount)
    }
}