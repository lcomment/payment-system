package com.example.payment.monolith.common.idempotency

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "idempotency_keys")
data class IdempotencyKey(
    @Id
    val idempotencyKey: String,

    @Column(nullable = false)
    val eventType: String,

    @Column(nullable = false)
    val processedAt: LocalDateTime = LocalDateTime.now()
)
