package com.listik.bookservice.domain.port.output

import com.listik.bookservice.domain.model.BookRecord
import org.springframework.data.domain.Slice

interface BookRecordRepositoryPort {
    fun findAllByUserIdAndStatus(userId: Long, status: BookRecord.Status, page: Int, size: Int): Slice<BookRecord>
    fun findById(id: Long): BookRecord?
    fun save(record: BookRecord): BookRecord
    fun deleteById(id: Long)
    fun searchByUserIdAndKeyword(userId: Long, keyword: String): List<BookRecord>
}