package com.listik.apiservice.common.dto

data class ApiResponse<T>(
    val code: String = "SUCCESS",
    val data: T? = null,
    val messageKey: String? = null
) {
    companion object {
        fun <T> success(data: T, messageKey: String? = null): ApiResponse<T> =
            ApiResponse(code = "SUCCESS", data = data, messageKey = messageKey)
    }
}
