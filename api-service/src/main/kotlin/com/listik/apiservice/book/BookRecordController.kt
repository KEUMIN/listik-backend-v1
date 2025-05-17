package com.listik.apiservice.book

import com.listik.apiservice.book.dto.request.CreateBookRequest
import com.listik.apiservice.book.dto.request.UpdateBookRequest
import com.listik.apiservice.book.dto.response.BookResponse
import com.listik.apiservice.common.dto.ApiResponse
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/book-records")
class BookRecordController(
    private val service: BookRecordUseCase
) {

    @PostMapping
    fun create(@RequestBody request: CreateBookRequest): ResponseEntity<ApiResponse<BookResponse>> {
        val created = service.create(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                BookResponse.from(created),
                messageKey = "book.created"
            ))
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val updated = service.update(id, request.toCommand())
        return ResponseEntity
            .ok(ApiResponse.success(
                BookResponse.from(updated),
                messageKey = "book.updated"
            ))
    }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponse>> {
        val book = service.getOne(id)
        return ResponseEntity
            .ok(ApiResponse.success(BookResponse.from(book)))
    }

    @GetMapping("/user/{userId}")
    fun getAllByUser(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val list = service.getAllByUser(userId, page, size)
            .map(BookResponse::from)
        return ResponseEntity
            .ok(ApiResponse.success(list))
    }

    @GetMapping("/search")
    fun search(
        @RequestParam userId: Long,
        @RequestParam keyword: String
    ): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val results = service.search(userId, keyword)
            .map(BookResponse::from)
        return ResponseEntity
            .ok(ApiResponse.success(results))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success(Unit))
    }
}

