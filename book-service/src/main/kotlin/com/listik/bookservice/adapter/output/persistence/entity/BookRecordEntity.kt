package com.listik.bookservice.adapter.output.persistence.entity

import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.model.BookRecord
import com.listik.coreservice.entity.BaseEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "book_record")
class BookRecordEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var title: String,

    var author: String? = null,

    var coverUrl: String? = null,

    var isbn: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BookRecordStatus = BookRecordStatus.TO_READ,

    @Column(nullable = false)
    var startedAt: Instant,

    var completedAt: Instant? = null,

    var rating: Double? = null,

    @Column(nullable = false)
    var totalPageNumber: Int,

    @Column(nullable = false)
    var currentPageNumber: Int,

    @Lob
    var review: String? = null
) : BaseEntity() {
    fun toDomain(): BookRecord =
        BookRecord(
            id,
            userId,
            title,
            author,
            coverUrl,
            isbn,
            status,
            startedAt,
            completedAt,
            rating,
            review,
            totalPageNumber,
            currentPageNumber
        )

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
                rating = domain.rating,
                review = domain.review,
                totalPageNumber = domain.totalPageNumber,
                currentPageNumber = domain.currentPageNumber
            )
    }
}