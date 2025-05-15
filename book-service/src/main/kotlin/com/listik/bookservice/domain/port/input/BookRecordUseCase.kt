package com.listik.bookservice.domain.port.input

import com.listik.bookservice.domain.model.BookRecord

interface BookRecordUseCase {
    fun getAllByUser(userId: Long, page: Int, size: Int): List<BookRecord>
    fun getOne(id: Long): BookRecord
    fun create(command: CreateBookCommand): BookRecord
    fun update(id: Long, command: UpdateBookCommand): BookRecord
    fun delete(id: Long)
    fun search(userId: Long, keyword: String): List<BookRecord>
}
