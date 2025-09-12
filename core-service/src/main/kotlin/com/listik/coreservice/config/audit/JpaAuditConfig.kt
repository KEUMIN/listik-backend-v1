package com.listik.coreservice.config.audit

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing(
    auditorAwareRef = "auditorAware",
    dateTimeProviderRef = "dateTimeProvider"
)
class JpaAuditConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> = CustomAuditorAware()

    @Bean
    fun dateTimeProvider(): DateTimeProvider = CustomDateTimeProvider()
}