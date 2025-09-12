package com.listik.bookservice.domain.port.output

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery
import org.springframework.data.domain.Slice

interface BookRecordRepositoryPort {
    fun findById(id: Long): BookRecord?
    fun findAllByQuery(query: GetBookRecordsQuery): Slice<BookRecord>
    fun save(record: BookRecord): BookRecord
    fun deleteById(id: Long)
}