package com.listik.bookservice.domain.port.input

import com.listik.bookservice.domain.model.BookRecord
import org.springframework.data.domain.Slice

interface BookRecordUseCase {
    fun getAllByUserAndStatus(userId: Long, status: BookRecord.Status, page: Int, size: Int): Slice<BookRecord>
    fun getOne(id: Long): BookRecord
    fun create(command: CreateBookCommand): BookRecord
    fun update(id: Long, command: UpdateBookCommand): BookRecord
    fun delete(id: Long)
    fun search(userId: Long, keyword: String): List<BookRecord>
}
