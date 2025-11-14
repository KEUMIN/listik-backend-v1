package com.listik.bookservice.domain.port.input.command

import com.listik.bookservice.domain.eunum.BookRecordStatus
import java.time.Instant

data class CreateBookCommand(
    val userId: String,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val isbn: String?,
    val status: BookRecordStatus,
    val startedAt: Instant,
    val completedAt: Instant?,
    val rating: Double?,
    val review: String?,
    val totalPageNumber: Int,
    val currentPageNumber: Int,
)
