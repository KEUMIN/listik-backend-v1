package com.listik.bookservice.application.service

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import com.listik.bookservice.domain.port.input.CreateBookCommand
import com.listik.bookservice.domain.port.input.UpdateBookCommand
import com.listik.bookservice.domain.port.output.BookRecordRepositoryPort
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookRecordService(
    private val repository: BookRecordRepositoryPort
) : BookRecordUseCase {

    override fun getAllByUserAndStatus(userId: Long, status: BookRecord.Status, page: Int, size: Int): Slice<BookRecord> {
        return repository.findAllByUserIdAndStatus(userId, status, page, size)
    }

    override fun getOne(id: Long): BookRecord =
        repository.findById(id) ?: throw IllegalArgumentException("Book not found")

    override fun create(command: CreateBookCommand): BookRecord {
        val now = LocalDateTime.now()
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
                createdAt = now,
                updatedAt = now
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
        existing.updatedAt = LocalDateTime.now()
        return repository.save(existing)
    }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }

    override fun search(userId: Long, keyword: String): List<BookRecord> =
        repository.searchByUserIdAndKeyword(userId, keyword)
}
