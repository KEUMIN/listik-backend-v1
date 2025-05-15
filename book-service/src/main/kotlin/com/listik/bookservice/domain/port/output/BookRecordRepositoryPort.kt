package com.listik.bookservice.domain.port.output

import com.listik.bookservice.domain.model.BookRecord

interface BookRecordRepositoryPort {
    fun findAllByUserId(userId: Long, page: Int, size: Int): List<BookRecord>
    fun findById(id: Long): BookRecord?
    fun save(record: BookRecord): BookRecord
    fun deleteById(id: Long)
    fun searchByUserIdAndKeyword(userId: Long, keyword: String): List<BookRecord>
}