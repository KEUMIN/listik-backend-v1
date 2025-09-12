package com.listik.coreservice.config.audit

import org.springframework.data.auditing.DateTimeProvider
import java.time.ZonedDateTime
import java.time.temporal.TemporalAccessor
import java.util.*

class CustomDateTimeProvider : DateTimeProvider {
    override fun getNow(): Optional<TemporalAccessor> = Optional.of(ZonedDateTime.now())
}