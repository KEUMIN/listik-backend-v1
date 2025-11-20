package com.listik.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class AuthServiceApplication

// Confirmation commit to trigger deployment : 2025-11-20 10:05
fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}