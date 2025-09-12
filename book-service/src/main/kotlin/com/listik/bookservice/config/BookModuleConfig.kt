package com.listik.userservice.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.listik.bookservice.adapter.output.persistence"])
@EntityScan("com.listik.bookservice.adapter.output.persistence.entity")
class BookModuleConfig