package com.listik.apiservice.book.dto.request

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.UpdateBookCommand
import java.time.LocalDateTime

data class UpdateBookRequest(
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val isbn: String?,
    val status: BookRecord.Status,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val rating: Int?,
    val review: String?
) {
    fun toCommand() = UpdateBookCommand(
        title, author, coverUrl, isbn, status,
        startedAt, completedAt, rating, review
    )
}
