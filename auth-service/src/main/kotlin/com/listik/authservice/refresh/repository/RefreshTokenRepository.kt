package com.listik.authservice.refresh.repository

import com.listik.authservice.refresh.model.RefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByToken(token: String): RefreshToken?
    fun findByUserId(userId: UUID): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteByUserId(userId: UUID)
}
