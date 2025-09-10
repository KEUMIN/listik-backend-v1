package com.listik.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class AuthServiceApplication

fun main(args: Array<String>) {
//    run entire application with: ./gradlew :auth-service:bootRun
    runApplication<AuthServiceApplication>(*args)
}