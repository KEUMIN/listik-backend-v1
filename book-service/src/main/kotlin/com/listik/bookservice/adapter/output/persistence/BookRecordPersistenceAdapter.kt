package com.listik.bookservice.adapter.output.persistence

import com.listik.bookservice.adapter.output.persistence.entity.BookRecordEntity
import com.listik.bookservice.adapter.output.persistence.jpa.BookRecordJpaRepository
import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery
import com.listik.bookservice.domain.port.output.BookRecordRepositoryPort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class BookRecordPersistenceAdapter(
    private val jpaRepository: BookRecordJpaRepository
) : BookRecordRepositoryPort {

    override fun findAllByQuery(query: GetBookRecordsQuery): Slice<BookRecord> {
        val pageable = PageRequest.of(query.page, query.size, Sort.by("id").descending())
        return (if (query.title != null)
            jpaRepository.findAllByUserIdAndStatusAndTitleContaining(
                query.userId,
                query.status,
                query.title,
                pageable
            )
        else jpaRepository.findAllByUserIdAndStatus(
            query.userId,
            query.status,
            pageable
        ))
            .map { it.toDomain() }
    }

    override fun findById(id: Long): BookRecord? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun save(record: BookRecord): BookRecord =
        jpaRepository.save(BookRecordEntity.from(record)).toDomain()

    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: String) {
        jpaRepository.deleteByUserId(userId)
    }
}
