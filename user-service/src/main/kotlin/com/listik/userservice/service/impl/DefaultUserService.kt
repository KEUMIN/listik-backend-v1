package com.listik.userservice.service.impl

import com.listik.userservice.entity.AuthAccountEntity
import com.listik.userservice.entity.UserEntity
import com.listik.userservice.repository.AuthAccountRepository
import com.listik.userservice.repository.UserRepository
import com.listik.userservice.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DefaultUserService (
    private val userRepository: UserRepository,
    private val authAccountRepository: AuthAccountRepository
): UserService {
    override fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    override fun findAuthAccountByProvider(provider: String, providerUserId: String): AuthAccountEntity? {
        return authAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
    }

    override fun saveAuthAccount(authAccountEntity: AuthAccountEntity): AuthAccountEntity {
        return authAccountRepository.save(authAccountEntity)
    }

    @Transactional
    override fun createUserWithAuthAccount(
        provider: String,
        providerUserId: String
    ): Pair<UserEntity, AuthAccountEntity> {
        val user = userRepository.save(UserEntity())
        val authAccount = authAccountRepository.save(
            AuthAccountEntity(
                user = user,
                provider = provider,
                providerUserId = providerUserId
            )
        )
        return Pair(user, authAccount)
    }

    @Transactional
    override fun update(userId: UUID, nickName: String?): UserEntity {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }
        user.nickName = nickName
        return userRepository.save(user)
    }
}