package com.listik.userservice.dto.response

import com.listik.userservice.entity.UserEntity
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val nickName: String?
) {
    companion object {
        fun from(user: UserEntity) = UserResponse(
            id = user.id!!,
            nickName = user.nickName
        )
    }
}