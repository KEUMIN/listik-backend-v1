package com.listik.bookservice.adapter.input.endpoint.dto.request

import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.port.input.command.CreateBookCommand
import java.time.LocalDateTime
import java.time.ZoneId

data class CreateBookRecordRequest(
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val isbn: String?,
    val status: BookRecordStatus,
    val startedAt: LocalDateTime,
    val completedAt: LocalDateTime?,
    val rating: Double?,
    val review: String?,
    val totalPageNumber: Int,
    val currentPageNumber: Int = 0,
) {
    fun toCommand(userId: String, zoneId: String) = CreateBookCommand(
        userId = userId,
        title = title,
        author = author,
        coverUrl = coverUrl,
        isbn = isbn,
        status = status,
        startedAt = startedAt.atZone(ZoneId.of(zoneId)).toInstant(),
        completedAt = completedAt?.atZone(ZoneId.of(zoneId))?.toInstant(),
        rating = rating,
        review = review,
        totalPageNumber = totalPageNumber,
        currentPageNumber = currentPageNumber,
    )
}