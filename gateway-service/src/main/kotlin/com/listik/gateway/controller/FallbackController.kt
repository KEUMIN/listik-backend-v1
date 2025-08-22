package com.listik.gateway.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class FallbackController {
    @GetMapping("/fallback")
    fun fallback(): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(mapOf("message" to "fallback", "timestamp" to Instant.now().toString()))
}
