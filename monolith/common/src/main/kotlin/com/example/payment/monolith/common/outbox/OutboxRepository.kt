package com.example.payment.monolith.common.outbox

import org.springframework.data.jpa.repository.JpaRepository

interface OutboxRepository : JpaRepository<OutboxEvent, Long> {
    fun findByStatusAndRetryCountLessThan(status: OutboxStatus, retryCount: Int): List<OutboxEvent>
}
