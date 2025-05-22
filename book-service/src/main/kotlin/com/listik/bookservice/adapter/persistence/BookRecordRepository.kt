package com.listik.bookservice.adapter.persistence

import com.listik.bookservice.adapter.persistence.entity.BookRecordEntity
import com.listik.bookservice.domain.model.BookRecord
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.Query

interface BookRecordRepository : org.springframework.data.jpa.repository.JpaRepository<BookRecordEntity, Long> {
    fun findAllByUserIdAndStatus(userId: Long, status: BookRecord.Status, pageable: Pageable): Slice<BookRecordEntity>

    @Query("SELECT b FROM BookRecordEntity b WHERE b.userId = :userId AND (b.title LIKE :keyword OR b.author LIKE :keyword)")
    fun searchByUserIdAndKeyword(userId: Long, keyword: String): List<BookRecordEntity>
}
