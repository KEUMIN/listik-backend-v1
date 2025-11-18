package com.listik.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GatewayApplication

// Confirmation commit to trigger deployment : 2025-11-18 21:31
fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}