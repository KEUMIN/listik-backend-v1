package com.listik.bookservice.domain.port.input.query

import com.listik.bookservice.domain.eunum.BookRecordStatus

data class GetBookRecordsQuery(
    val userId: String,
    val status: BookRecordStatus,
    val title: String? = null,
    val page: Int,
    val size: Int,
)