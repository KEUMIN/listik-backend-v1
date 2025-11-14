package com.listik.bookservice.domain.model

import com.listik.bookservice.domain.eunum.BookRecordStatus
import java.time.Instant

class BookRecord(
    val id: Long? = null,
    val userId: String,
    var title: String,
    var author: String?,
    var coverUrl: String?,
    var isbn: String?,
    var status: BookRecordStatus,
    var startedAt: Instant,
    var completedAt: Instant?,
    var rating: Double?,
    var review: String?,
    var totalPageNumber: Int,
    var currentPageNumber: Int,
)