package com.listik.authservice.client

import com.listik.authservice.config.FeignConfig
import com.listik.coreservice.dto.ApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "user-service", url = "\${services.user-service.url}", configuration = [FeignConfig::class])
interface UserServiceClient {

    @GetMapping("/users/auth-account/email/{email}")
    fun findAuthAccountByEmail(@PathVariable email: String): ApiResponse<AuthAccountDto?>

    @GetMapping("/users/auth-account/provider/{provider}/{providerUserId}")
    fun findAuthAccountByProvider(
        @PathVariable provider: String,
        @PathVariable providerUserId: String
    ): ApiResponse<AuthAccountDto?>

    @PostMapping("/users/create-with-auth")
    fun createUserWithAuthAccount(@RequestBody request: CreateUserWithAuthRequest): ApiResponse<CreateUserWithAuthResponse>

    @PutMapping("/users/auth-account/{id}")
    fun updateAuthAccount(@PathVariable id: Long, @RequestBody authAccount: AuthAccountDto): ApiResponse<AuthAccountDto>
}

data class AuthAccountDto(
    val id: Long?,
    val email: String?,
    val passwordHash: String?,
    val provider: String?,
    val providerUserId: String?,
    val userId: Long?
)

data class CreateUserWithAuthRequest(
    val nickname: String,
    val email: String,
    val passwordHash: String?,
    val provider: String?,
    val providerUserId: String?
)

data class CreateUserWithAuthResponse(
    val userId: Long,
    val authAccountId: Long
)