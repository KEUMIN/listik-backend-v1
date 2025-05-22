package com.listik.bookservice.adapter.persistence

import com.listik.bookservice.adapter.persistence.entity.BookRecordEntity
import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.output.BookRecordRepositoryPort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class BookRecordJpaAdapter(
    private val jpaRepository: BookRecordRepository
) : BookRecordRepositoryPort {

    override fun findAllByUserIdAndStatus(userId: Long, status: BookRecord.Status, page: Int, size: Int): Slice<BookRecord> {
        val pageable = PageRequest.of(page, size)
        return jpaRepository.findAllByUserIdAndStatus(userId, status, pageable)
            .map { it.toDomain() }
    }

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
