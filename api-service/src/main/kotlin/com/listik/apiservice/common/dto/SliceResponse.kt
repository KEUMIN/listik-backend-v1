package com.listik.apiservice.common.dto

data class SliceResponse<T>(
    val content: List<T>,
    val hasNext: Boolean
)

