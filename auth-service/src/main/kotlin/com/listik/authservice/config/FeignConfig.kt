package com.listik.authservice.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {

    @Bean
    fun serviceAuthRequestInterceptor(serviceAuthInterceptor: ServiceAuthInterceptor): RequestInterceptor {
        return serviceAuthInterceptor
    }
}