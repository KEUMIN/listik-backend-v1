package com.listik.bookservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.listik"])
class BookServiceApplication

// Confirmation commit to trigger deployment : 2025-11-19 22:03
fun main(args: Array<String>) {
    runApplication<BookServiceApplication>(*args)
}