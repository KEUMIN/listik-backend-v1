package com.listik.bookservice.adapter.persistence.entity

import com.listik.bookservice.domain.model.BookRecord
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "book_record")
class BookRecordEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var userId: Long? = null,

    @Column(nullable = false)
    var title: String? = null,

    var author: String? = null,

    var coverUrl: String? = null,

    var isbn: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookRecord.Status = BookRecord.Status.TO_READ,

    var startedAt: LocalDateTime? = null,

    var completedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var updatedAt: LocalDateTime? = null,

    var rating: Int? = null,

    @Lob
    var review: String? = null
) {
    fun toDomain(): BookRecord =
        BookRecord(id, userId!!, title!!, author, coverUrl, isbn, status, startedAt, completedAt, createdAt, updatedAt, rating, review)

    companion object {
        fun from(domain: BookRecord): BookRecordEntity =
            BookRecordEntity(
                id = domain.id,
                userId = domain.userId,
                title = domain.title,
                author = domain.author,
                coverUrl = domain.coverUrl,
                isbn = domain.isbn,
                status = domain.status,
                startedAt = domain.startedAt,
                completedAt = domain.completedAt,
                createdAt = domain.createdAt,
                updatedAt = domain.updatedAt,
                rating = domain.rating,
                review = domain.review
            )
    }
}