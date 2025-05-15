package com.listik.bookservice.adapter.web

import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import com.listik.bookservice.domain.port.input.CreateBookCommand
import com.listik.bookservice.domain.port.input.UpdateBookCommand
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/book-records")
class BookRecordController(
    private val service: BookRecordUseCase
) {

    @GetMapping("/user/{userId}")
    fun getAll(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): List<BookRecord> =
        service.getAllByUser(userId, page, size)

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): BookRecord =
        service.getOne(id)

    @PostMapping
    fun create(@RequestBody command: CreateBookCommand): BookRecord =
        service.create(command)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody command: UpdateBookCommand): BookRecord =
        service.update(id, command)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) =
        service.delete(id)

    @GetMapping("/search")
    fun search(
        @RequestParam userId: Long,
        @RequestParam keyword: String
    ): List<BookRecord> =
        service.search(userId, keyword)
}
