package com.listik.bookservice.adapter.output.persistence.jpa

import com.listik.bookservice.adapter.output.persistence.entity.BookRecordEntity
import com.listik.bookservice.domain.eunum.BookRecordStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.CrudRepository

interface BookRecordJpaRepository :
    CrudRepository<BookRecordEntity, Long> {
    fun findAllByUserIdAndStatus(
        userId: String,
        status: BookRecordStatus,
        pageable: Pageable
    ): Slice<BookRecordEntity>

    fun findAllByUserIdAndStatusAndTitleContaining(
        userId: String,
        status: BookRecordStatus,
        title: String,
        pageable: Pageable
    ): Slice<BookRecordEntity>
}
