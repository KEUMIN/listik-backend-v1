package com.listik.coreservice.dto

data class SliceResponse<T>(
    val content: List<T>,
    val hasNext: Boolean
)