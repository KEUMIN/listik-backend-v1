package com.listik.userservice.repository

import com.listik.userservice.entity.AuthAccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AuthAccountRepository : JpaRepository<AuthAccountEntity, Long> {
    fun findByProviderAndProviderUserId(provider: String, providerUserId: String): AuthAccountEntity?
}