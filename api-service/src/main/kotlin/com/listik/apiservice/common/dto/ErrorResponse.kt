package com.listik.apiservice.common.dto

import com.listik.apiservice.common.enum.ErrorCode
import java.time.LocalDateTime

data class ErrorResponse(
    val code: String,
    val userMessageKey: String,
    val developerMessage: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(
            code: String,
            userMessageKey: String,
            developerMessage: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                code = code,
                userMessageKey = userMessageKey,
                developerMessage = developerMessage
            )
        }

        fun fromErrorCode(
            errorCode: ErrorCode,
            developerMessage: String? = null
        ): ErrorResponse {
            return ErrorResponse(
                code = errorCode.code,
                userMessageKey = errorCode.userMessageKey,
                developerMessage = developerMessage
            )
        }
    }
}

