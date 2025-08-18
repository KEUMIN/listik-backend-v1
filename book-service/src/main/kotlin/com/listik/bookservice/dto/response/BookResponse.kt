package com.listik.bookservice.dto.response

import com.listik.bookservice.domain.model.BookRecord
import java.time.LocalDateTime

data class BookResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val isbn: String?,
    val status: BookRecord.Status,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val rating: Int?,
    val review: String?
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
                createdAt = book.createdAt,
                updatedAt = book.updatedAt,
                rating = book.rating,
                review = book.review
            )
    }
}