package com.example.payment.monolith.wallet.domain.event

import com.example.payment.monolith.common.event.DomainEvent
import java.time.LocalDateTime

data class WalletSettledEvent(
    override val aggregateId: String,
    override val eventId: String,
    override val occurredAt: LocalDateTime,
    val orderId: String
) : DomainEvent
