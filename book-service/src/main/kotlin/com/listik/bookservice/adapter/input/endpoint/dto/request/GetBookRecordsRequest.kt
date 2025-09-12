package com.listik.bookservice.adapter.input.endpoint.dto.request

import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery

data class GetBookRecordsRequest(
    val userId: Long,
    val status: BookRecordStatus,
    val title: String?,
    val page: Int,
    val size: Int,
) {
    fun toQuery() = GetBookRecordsQuery(
        userId = userId,
        status = status,
        title = title,
        page = page,
        size = size,
    )
}
