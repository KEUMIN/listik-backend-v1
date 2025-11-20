package com.listik.userservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(scanBasePackages = ["com.listik"])
@EnableFeignClients
class UserServiceApplication

// Confirmation commit to trigger deployment : 2025-11-20 09:51
fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}