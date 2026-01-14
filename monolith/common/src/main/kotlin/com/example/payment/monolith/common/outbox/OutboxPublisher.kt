package com.example.payment.monolith.common.outbox

import com.example.payment.monolith.common.event.DomainEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxPublisher(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper
) {
    @Transactional
    fun publish(event: DomainEvent) {
        val outboxEvent = OutboxEvent(
            aggregateId = event.aggregateId,
            eventType = event::class.simpleName ?: "UnknownEvent",
            payload = objectMapper.writeValueAsString(event),
            status = OutboxStatus.PENDING
        )
        outboxRepository.save(outboxEvent)
    }
}
