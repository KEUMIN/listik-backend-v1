package com.listik.userservice.controller

import com.listik.coreservice.dto.ApiResponse
import com.listik.userservice.dto.request.CreateUserWithAuthRequest
import com.listik.userservice.dto.response.AuthAccountDto
import com.listik.userservice.dto.response.CreateUserWithAuthResponse
import com.listik.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserInternalController(
    private val userService: UserService
) {

    @GetMapping("/auth-account/email/{email}")
    fun findAuthAccountByEmail(@PathVariable email: String): ResponseEntity<ApiResponse<AuthAccountDto?>> {
        val authAccount = userService.findAuthAccountByEmail(email)
        val dto = authAccount?.let {
            AuthAccountDto(
                id = it.id,
                email = it.email,
                passwordHash = it.passwordHash,
                provider = it.provider,
                providerUserId = it.providerUserId,
                userId = it.user?.id
            )
        }
        return ResponseEntity.ok(ApiResponse.success(dto))
    }

    @GetMapping("/auth-account/provider/{provider}/{providerUserId}")
    fun findAuthAccountByProvider(
        @PathVariable provider: String,
        @PathVariable providerUserId: String
    ): ResponseEntity<ApiResponse<AuthAccountDto?>> {
        val authAccount = userService.findAuthAccountByProvider(provider, providerUserId)
        val dto = authAccount?.let {
            AuthAccountDto(
                id = it.id,
                email = it.email,
                passwordHash = it.passwordHash,
                provider = it.provider,
                providerUserId = it.providerUserId,
                userId = it.user?.id
            )
        }
        return ResponseEntity.ok(ApiResponse.success(dto))
    }

    @PostMapping("/create-with-auth")
    fun createUserWithAuthAccount(@RequestBody request: CreateUserWithAuthRequest): ResponseEntity<ApiResponse<CreateUserWithAuthResponse>> {
        val (user, authAccount) = userService.createUserWithAuthAccount(
            nickname = request.nickname,
            email = request.email,
            passwordHash = request.passwordHash,
            provider = request.provider,
            providerUserId = request.providerUserId
        )
        
        val response = CreateUserWithAuthResponse(
            userId = user.id!!,
            authAccountId = authAccount.id!!
        )
        
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/auth-account/{id}")
    fun updateAuthAccount(@PathVariable id: Long, @RequestBody authAccountDto: AuthAccountDto): ResponseEntity<ApiResponse<AuthAccountDto>> {
        val authAccount = userService.findAuthAccountByEmail(authAccountDto.email!!)
            ?: throw IllegalArgumentException("AuthAccount not found")
        
        authAccount.provider = authAccountDto.provider
        authAccount.providerUserId = authAccountDto.providerUserId
        
        val updatedAccount = userService.saveAuthAccount(authAccount)
        
        val dto = AuthAccountDto(
            id = updatedAccount.id,
            email = updatedAccount.email,
            passwordHash = updatedAccount.passwordHash,
            provider = updatedAccount.provider,
            providerUserId = updatedAccount.providerUserId,
            userId = updatedAccount.user?.id
        )
        
        return ResponseEntity.ok(ApiResponse.success(dto))
    }
}