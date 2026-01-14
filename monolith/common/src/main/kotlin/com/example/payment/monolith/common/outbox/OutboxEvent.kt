package com.example.payment.monolith.common.outbox

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "outbox_events")
data class OutboxEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val aggregateId: String,

    @Column(nullable = false)
    val eventType: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val payload: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OutboxStatus = OutboxStatus.PENDING,

    @Column(nullable = true)
    val sqsMessageId: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    val sentAt: LocalDateTime? = null,

    @Column(nullable = false)
    val retryCount: Int = 0
)

enum class OutboxStatus {
    PENDING, SENT, FAILED
}
