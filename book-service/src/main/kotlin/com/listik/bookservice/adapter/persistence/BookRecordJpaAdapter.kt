package com.listik.bookservice.adapter.persistence

import com.listik.bookservice.adapter.persistence.entity.BookRecordEntity
import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.output.BookRecordRepositoryPort
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class BookRecordJpaAdapter(
    private val jpaRepository: BookRecordRepository
) : BookRecordRepositoryPort {

    override fun findAllByUserId(userId: Long, page: Int, size: Int): List<BookRecord> =
        jpaRepository.findAllByUserId(userId, PageRequest.of(page, size)).map { it.toDomain() }

    override fun findById(id: Long): BookRecord? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun save(record: BookRecord): BookRecord =
        jpaRepository.save(BookRecordEntity.from(record)).toDomain()

    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }

    override fun searchByUserIdAndKeyword(userId: Long, keyword: String): List<BookRecord> =
        jpaRepository.searchByUserIdAndKeyword(userId, "%$keyword%").map { it.toDomain() }
}
