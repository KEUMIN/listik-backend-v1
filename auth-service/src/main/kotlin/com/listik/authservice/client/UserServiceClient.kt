package com.listik.authservice.client

import com.listik.authservice.config.FeignConfig
import com.listik.coreservice.dto.ApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import java.util.UUID

@FeignClient(name = "user-service", url = "\${services.user-service.url}", configuration = [FeignConfig::class])
interface UserServiceClient {

    @GetMapping("/users/auth-account/provider/{provider}/{providerUserId}")
    fun findAuthAccountByProvider(
        @PathVariable provider: String,
        @PathVariable providerUserId: String
    ): ApiResponse<AuthAccountDto?>

    @PostMapping("/users/create-with-auth")
    fun createUserWithAuthAccount(@RequestBody request: CreateUserWithAuthRequest): ApiResponse<CreateUserWithAuthResponse>
}

data class AuthAccountDto(
    val id: Long?,
    val provider: String,
    val providerUserId: String,
    val userId: UUID?
)

data class CreateUserWithAuthRequest(
    val provider: String,
    val providerUserId: String
)

data class CreateUserWithAuthResponse(
    val userId: UUID,
    val authAccountId: Long
)