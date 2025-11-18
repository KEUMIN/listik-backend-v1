package com.listik.bookservice.adapter.input.endpoint

import com.listik.bookservice.adapter.input.endpoint.dto.request.CreateBookRecordRequest
import com.listik.bookservice.adapter.input.endpoint.dto.request.UpdateBookRequest
import com.listik.bookservice.adapter.input.endpoint.dto.response.BookResponse
import com.listik.bookservice.domain.eunum.BookRecordStatus
import com.listik.bookservice.domain.port.input.BookRecordUseCase
import com.listik.bookservice.domain.port.input.query.GetBookRecordsQuery
import com.listik.coreservice.dto.ApiResponse
import com.listik.coreservice.dto.SliceResponse
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
    companion object {
        private const val HEADER_USER_ID_KEY = "X-User-Id"
        private const val HEADER_TIMEZONE_KEY = "X-Timezone"
    }

    @Operation(summary = "단일 책 조회", description = "ID로 책 하나를 조회합니다.")
    @GetMapping("/book-records/{id}")
    fun getOne(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponse>> {
        val book = service.getOne(id)
        return ResponseEntity
            .ok(ApiResponse.success(BookResponse.from(book)))
    }

    @Operation(summary = "상태별 책 목록 조회 (스크롤 페이징)", description = "유저 ID와 상태에 따라 책 목록을 페이징 조회합니다.")
    @GetMapping("/book-records")
    fun getAllByQuery(
        @RequestHeader(HEADER_USER_ID_KEY) userId: String,
        @RequestParam status: BookRecordStatus,
        @RequestParam title: String?,
        @RequestParam page: Int,
        @RequestParam size: Int,
    ): ResponseEntity<ApiResponse<SliceResponse<BookResponse>>> {
        val slice = service.getAllByQuery(
            query = GetBookRecordsQuery(
                userId = userId,
                status = status,
                title = title,
                page = page,
                size = size,
            )
        )
        val dtoSlice = SliceResponse(
            content = slice.content.map(BookResponse::from),
            hasNext = slice.hasNext()
        )
        return ResponseEntity.ok(ApiResponse.success(dtoSlice))
    }

    @Operation(summary = "책 생성", description = "새 책을 등록합니다.")
    @PostMapping("/book-records")
    fun create(
        @RequestHeader(HEADER_USER_ID_KEY) userId: String,
        @RequestHeader(HEADER_TIMEZONE_KEY) zoneId: String,
        @RequestBody request: CreateBookRecordRequest,
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val created = service.create(
            request.toCommand(
                userId = userId,
                zoneId = zoneId,
            )
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiResponse.success(
                    BookResponse.from(created),
                    messageKey = "book.created"
                )
            )
    }

    @Operation(summary = "책 수정", description = "책 정보를 수정합니다.")
    @PutMapping("/book-records/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestHeader(HEADER_TIMEZONE_KEY) zoneId: String,
        @RequestBody request: UpdateBookRequest,
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val updated = service.update(id, request.toCommand(zoneId))
        return ResponseEntity
            .ok(
                ApiResponse.success(
                    BookResponse.from(updated),
                    messageKey = "book.updated"
                )
            )
    }

    @Operation(summary = "책 삭제", description = "책 기록을 삭제합니다.")
    @DeleteMapping("/book-records/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        service.delete(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success(Unit))
    }

    @Operation(summary = "사용자 책 레코드 전체 삭제", description = "사용자의 모든 책 레코드를 삭제합니다. (회원 탈퇴 시 호출)")
    @DeleteMapping("/book-records/user/{userId}")
    fun deleteUserBookRecords(@PathVariable userId: String): ResponseEntity<ApiResponse<Unit>> {
        service.deleteByUserId(userId)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success(Unit))
    }
}