package com.listik.coreservice.enum

enum class ErrorCode(val code: String, val userMessageKey: String) {
    BOOK_NOT_FOUND("BOOK_NOT_FOUND", "error.book.not_found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "error.validation.generic"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "error.internal.server_error")
}