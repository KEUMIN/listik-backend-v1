package com.listik.userservice.client

import com.listik.coreservice.dto.ApiResponse
import com.listik.userservice.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient(name = "book-service", url = "\${services.book-service.url}", configuration = [FeignConfig::class])
interface BookServiceClient {

    @DeleteMapping("/books/book-records/user/{userId}")
    fun deleteUserBookRecords(@PathVariable userId: UUID): ApiResponse<Unit>
}
