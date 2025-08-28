package com.listik.authservice.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ServiceAuthInterceptor(
    @Value("\${service.auth.secret:default-service-secret-key}") 
    private val serviceAuthSecret: String
) : RequestInterceptor {

    companion object {
        private const val SERVICE_AUTH_HEADER = "X-Service-Auth"
    }

    override fun apply(template: RequestTemplate) {
        template.header(SERVICE_AUTH_HEADER, serviceAuthSecret)
    }
}