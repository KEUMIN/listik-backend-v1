package com.listik.bookservice.controller

import com.listik.bookservice.dto.request.CreateBookRequest
import com.listik.bookservice.dto.request.UpdateBookRequest
import com.listik.bookservice.dto.response.BookResponse
import com.listik.coreservice.dto.ApiResponse
import com.listik.coreservice.dto.SliceResponse
import com.listik.bookservice.domain.model.BookRecord
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/books")
@Tag(name = "책 기록 API", description = "책 생성/조회/수정/삭제 및 검색 API")
class BookRecordController(
    private val service: BookRecordUseCase
) {
    @Operation(summary = "책 생성", description = "새 책을 등록합니다.")
    @PostMapping("/book-records")
    fun create(@RequestBody request: CreateBookRequest): ResponseEntity<ApiResponse<BookResponse>> {
        val created = service.create(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                BookResponse.from(created),
                messageKey = "book.created"
            ))
    }
    @Operation(summary = "책 수정", description = "책 정보를 수정합니다.")
    @PutMapping("/book-records/{id}")
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
    @Operation(summary = "단일 책 조회", description = "ID로 책 하나를 조회합니다.")
    @GetMapping("/book-records/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponse>> {
        val book = service.getOne(id)
        return ResponseEntity
            .ok(ApiResponse.success(BookResponse.from(book)))
    }
    @Operation(summary = "상태별 책 목록 조회 (스크롤 페이징)", description = "유저 ID와 상태에 따라 책 목록을 페이징 조회합니다.")
    @GetMapping("/book-records/user/{userId}/status")
    fun getAllByUserAndStatus(
        @PathVariable userId: Long,
        @RequestParam status: BookRecord.Status,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<SliceResponse<BookResponse>>> {
        val slice = service.getAllByUserAndStatus(userId, status, page, size)
        val dtoSlice = SliceResponse(
            content = slice.content.map(BookResponse::from),
            hasNext = slice.hasNext()
        )
        return ResponseEntity.ok(ApiResponse.success(dtoSlice))
    }

    @Operation(summary = "책 검색", description = "제목/작가 키워드로 검색")
    @GetMapping("/book-records/search")
    fun search(
        @RequestParam userId: Long,
        @RequestParam keyword: String
    ): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val results = service.search(userId, keyword)
            .map(BookResponse::from)
        return ResponseEntity
            .ok(ApiResponse.success(results))
    }
    @Operation(summary = "책 삭제", description = "책 기록을 삭제합니다.")
    @DeleteMapping("/book-records/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success(Unit))
    }
}