package com.listik.bookservice.application

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import com.listik.bookservice.domain.port.input.command.CreateBookCommand
import com.listik.bookservice.domain.port.input.command.UpdateBookCommand
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery
import com.listik.bookservice.domain.port.output.BookRecordRepositoryPort
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class BookRecordService(
    private val repository: BookRecordRepositoryPort
) : BookRecordUseCase {

    override fun getOne(id: Long): BookRecord =
        repository.findById(id) ?: throw IllegalArgumentException("Book not found")

    override fun getAllByQuery(query: GetBookRecordsQuery): Slice<BookRecord> {
        return repository.findAllByQuery(query)
    }

    override fun create(command: CreateBookCommand): BookRecord {
        return repository.save(
            BookRecord(
                userId = command.userId,
                title = command.title,
                author = command.author,
                coverUrl = command.coverUrl,
                isbn = command.isbn,
                status = command.status,
                startedAt = command.startedAt,
                completedAt = command.completedAt,
                rating = command.rating,
                review = command.review,
                totalPageNumber = command.totalPageNumber,
                currentPageNumber = command.currentPageNumber,
            )
        )
    }

    override fun update(id: Long, command: UpdateBookCommand): BookRecord {
        val existing = repository.findById(id) ?: throw IllegalArgumentException("Book not found")
        existing.title = command.title
        existing.author = command.author
        existing.coverUrl = command.coverUrl
        existing.isbn = command.isbn
        existing.status = command.status
        existing.startedAt = command.startedAt
        existing.completedAt = command.completedAt
        existing.rating = command.rating
        existing.review = command.review
        existing.totalPageNumber = command.totalPageNumber
        existing.currentPageNumber = command.currentPageNumber
        return repository.save(existing)
    }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }

    override fun deleteByUserId(userId: String) {
        repository.deleteByUserId(userId)
    }
}
