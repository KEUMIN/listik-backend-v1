package com.listik.bookservice.domain.port.input

import com.listik.bookservice.domain.model.BookRecord
import java.time.LocalDateTime

data class CreateBookCommand(
    val userId: Long,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val isbn: String?,
    val status: BookRecord.Status,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val rating: Int?,
    val review: String?
)
