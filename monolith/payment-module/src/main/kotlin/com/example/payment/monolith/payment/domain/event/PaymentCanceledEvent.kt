package com.example.payment.monolith.payment.domain.event

import com.example.payment.monolith.common.event.DomainEvent
import com.example.payment.monolith.payment.domain.PaymentEvent
import java.time.LocalDateTime

data class PaymentCanceledEvent(
    override val aggregateId: String,
    override val eventId: String,
    override val occurredAt: LocalDateTime,
    val orderId: String,
    val paymentKey: String,
    val buyerId: Long,
    val cancelReason: String,
    val cancelAmount: Long,
    val paymentOrders: List<PaymentOrderInfo>
) : DomainEvent {
    companion object {
        fun from(paymentEvent: PaymentEvent, cancelReason: String): PaymentCanceledEvent {
            return PaymentCanceledEvent(
                aggregateId = paymentEvent.orderId,
                eventId = java.util.UUID.randomUUID().toString(),
                occurredAt = LocalDateTime.now(),
                orderId = paymentEvent.orderId,
                paymentKey = paymentEvent.paymentKey ?: "",
                buyerId = paymentEvent.buyerId,
                cancelReason = cancelReason,
                cancelAmount = paymentEvent.totalAmount(),
                paymentOrders = paymentEvent.paymentOrders.map {
                    PaymentOrderInfo(
                        id = it.id!!,
                        sellerId = it.sellerId,
                        amount = it.amount,
                        orderId = it.orderId
                    )
                }
            )
        }
    }
}
