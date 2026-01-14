package com.example.payment.monolith.common.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class InProcessEventBus(
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
