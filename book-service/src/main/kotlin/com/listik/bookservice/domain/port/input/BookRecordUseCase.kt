package com.listik.bookservice.domain.port.input

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.command.CreateBookCommand
import com.listik.bookservice.domain.port.input.command.UpdateBookCommand
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery
import org.springframework.data.domain.Slice

interface BookRecordUseCase {
    fun getOne(id: Long): BookRecord
    fun getAllByQuery(query: GetBookRecordsQuery): Slice<BookRecord>
    fun create(command: CreateBookCommand): BookRecord
    fun update(id: Long, command: UpdateBookCommand): BookRecord
    fun delete(id: Long)
}
