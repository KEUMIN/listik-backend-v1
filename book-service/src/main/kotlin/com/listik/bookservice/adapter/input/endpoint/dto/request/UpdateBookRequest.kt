package com.listik.bookservice.adapter.input.endpoint.dto.request

import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.port.input.command.UpdateBookCommand
import java.time.LocalDateTime
import java.time.ZoneId

data class UpdateBookRequest(
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
    val currentPageNumber: Int,
) {
    fun toCommand(zoneId: String) = UpdateBookCommand(
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