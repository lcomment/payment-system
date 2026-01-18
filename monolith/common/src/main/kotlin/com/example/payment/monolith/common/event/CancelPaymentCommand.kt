package com.example.payment.monolith.common.event

import java.time.LocalDateTime

/**
 * Fulfillment에서 지급 실패 시 발행하는 결제 취소 커맨드
 *
 * Outbox를 통해 payment-cancel-queue로 전송됨
 */
data class CancelPaymentCommand(
    override val aggregateId: String,
    override val eventId: String,
    override val occurredAt: LocalDateTime,
    val orderId: String,
    val paymentKey: String,
    val reason: CancelReason
) : DomainEvent

enum class CancelReason {
    FULFILLMENT_FAILED,      // 지급 즉시 실패
    FULFILLMENT_TIMEOUT,     // 지급 시도 중 타임아웃 (Recovery에서 감지)
    FULFILLMENT_UNKNOWN      // 지급 상태 불확정 (서버 다운 후 Recovery)
}