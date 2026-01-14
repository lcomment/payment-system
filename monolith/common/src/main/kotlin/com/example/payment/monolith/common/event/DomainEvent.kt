package com.example.payment.monolith.common.event

import java.time.LocalDateTime
import java.util.UUID

interface DomainEvent {
    val eventId: String get() = UUID.randomUUID().toString()
    val occurredAt: LocalDateTime get() = LocalDateTime.now()
    val aggregateId: String
}
