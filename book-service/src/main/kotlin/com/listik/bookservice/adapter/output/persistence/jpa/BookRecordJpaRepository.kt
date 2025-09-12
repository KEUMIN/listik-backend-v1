package com.listik.bookservice.adapter.output.persistence.jpa

import com.listik.bookservice.adapter.output.persistence.entity.BookRecordEntity
import com.listik.bookservice.domain.eunum.BookRecordStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface BookRecordJpaRepository :
    org.springframework.data.jpa.repository.JpaRepository<BookRecordEntity, Long> {
    fun findAllByUserIdAndStatus(
        userId: Long,
        status: BookRecordStatus,
        pageable: Pageable
    ): Slice<BookRecordEntity>

    fun findAllByUserIdAndStatusAndTitleContaining(
        userId: Long,
        status: BookRecordStatus,
        title: String,
        pageable: Pageable
    ): Slice<BookRecordEntity>
}
