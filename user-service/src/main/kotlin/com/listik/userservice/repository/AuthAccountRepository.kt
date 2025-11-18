package com.listik.userservice.repository

import com.listik.userservice.entity.AuthAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AuthAccountRepository : JpaRepository<AuthAccountEntity, Long> {
    fun findByProviderAndProviderUserId(provider: String, providerUserId: String): AuthAccountEntity?
    fun deleteByUserId(userId: UUID)
}