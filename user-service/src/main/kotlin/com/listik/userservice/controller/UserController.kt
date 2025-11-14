package com.listik.userservice.controller

import com.listik.coreservice.dto.ApiResponse
import com.listik.userservice.dto.request.CreateUserWithAuthRequest
import com.listik.userservice.dto.request.UpdateUserRequest
import com.listik.userservice.dto.response.AuthAccountDto
import com.listik.userservice.dto.response.CreateUserWithAuthResponse
import com.listik.userservice.dto.response.UserResponse
import com.listik.userservice.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    companion object {
        private const val HEADER_USER_ID_KEY = "X-User-Id"
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
                provider = it.provider,
                providerUserId = it.providerUserId,
                userId = it.user.id,
                role = it.role.toString()
            )
        }
        return ResponseEntity.ok(ApiResponse.success(dto))
    }

    @PostMapping("/create-with-auth")
    fun createUserWithAuthAccount(@RequestBody request: CreateUserWithAuthRequest): ResponseEntity<ApiResponse<CreateUserWithAuthResponse>> {
        val (user, authAccount) = userService.createUserWithAuthAccount(
            provider = request.provider,
            providerUserId = request.providerUserId
        )

        val response = CreateUserWithAuthResponse(
            userId = user.id!!,
            authAccountId = authAccount.id!!
        )

        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PutMapping("/update")
    fun update(
        @RequestHeader(HEADER_USER_ID_KEY) userId: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val userUUID = UUID.fromString(userId)
        val updatedUser = userService.update(userUUID, request.nickName)
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(updatedUser)))
    }
}