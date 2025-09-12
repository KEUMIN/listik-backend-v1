package com.listik.bookservice.adapter.input.endpoint.dto.response

import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.model.BookRecord
import java.time.Instant

data class BookResponse(
    val id: Long,
    val userId: Long,
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
) {
    companion object {
        fun from(book: BookRecord): BookResponse =
            BookResponse(
                id = book.id ?: throw IllegalStateException("Book ID must not be null"),
                userId = book.userId,
                title = book.title,
                author = book.author,
                coverUrl = book.coverUrl,
                isbn = book.isbn,
                status = book.status,
                startedAt = book.startedAt,
                completedAt = book.completedAt,
                rating = book.rating,
                review = book.review,
                totalPageNumber = book.totalPageNumber,
                currentPageNumber = book.currentPageNumber,
            )
    }
}