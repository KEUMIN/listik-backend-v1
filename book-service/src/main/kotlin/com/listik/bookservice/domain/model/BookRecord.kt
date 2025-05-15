package com.listik.bookservice.domain.model

import java.time.LocalDateTime

class BookRecord(
    val id: Long? = null,
    val userId: Long,
    var title: String,
    var author: String?,
    var coverUrl: String?,
    var isbn: String?,
    var status: Status,
    var startedAt: LocalDateTime?,
    var completedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
    var rating: Int?, // 1~5점
    var review: String?
) {
    enum class Status { TO_READ, READING, READ }
}