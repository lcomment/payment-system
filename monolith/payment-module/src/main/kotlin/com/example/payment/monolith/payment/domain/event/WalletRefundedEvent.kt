package com.example.payment.monolith.payment.domain.event

import com.example.payment.monolith.common.event.DomainEvent
import java.time.LocalDateTime

data class WalletRefundedEvent(
    override val aggregateId: String,
    override val eventId: String = java.util.UUID.randomUUID().toString(),
    override val occurredAt: LocalDateTime = LocalDateTime.now(),
    val orderId: String
) : DomainEvent
