package com.listik.bookservice.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "book_record")
open class BookRecord(

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
    var status: Status = Status.TO_READ,

    var startedAt: LocalDateTime? = null,

    var completedAt: LocalDateTime? = null,

    var createdAt: LocalDateTime? = null,

    var updatedAt: LocalDateTime? = null

) {
    // JPA가 사용하는 기본 생성자
    constructor() : this(
        id         = null,
        userId     = null,
        title      = null,
        author     = null,
        coverUrl   = null,
        isbn       = null,
        status     = Status.TO_READ,
        startedAt  = null,
        completedAt= null,
        createdAt  = null,
        updatedAt  = null
    )

    enum class Status {
        TO_READ,
        READING,
        READ
    }
}
