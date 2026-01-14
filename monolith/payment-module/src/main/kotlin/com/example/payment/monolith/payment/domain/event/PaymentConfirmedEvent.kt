package com.example.payment.monolith.payment.domain.event

import com.example.payment.monolith.common.event.DomainEvent
import com.example.payment.monolith.payment.domain.PaymentEvent
import java.time.LocalDateTime

data class PaymentConfirmedEvent(
    override val aggregateId: String,
    override val eventId: String,
    override val occurredAt: LocalDateTime,
    val orderId: String,
    val paymentKey: String,
    val buyerId: Long,
    val orderName: String,
    val totalAmount: Long,
    val paymentOrders: List<PaymentOrderInfo>
) : DomainEvent {
    companion object {
        fun from(paymentEvent: PaymentEvent): PaymentConfirmedEvent {
            return PaymentConfirmedEvent(
                aggregateId = paymentEvent.orderId,
                eventId = java.util.UUID.randomUUID().toString(),
                occurredAt = LocalDateTime.now(),
                orderId = paymentEvent.orderId,
                paymentKey = paymentEvent.paymentKey ?: "",
                buyerId = paymentEvent.buyerId,
                orderName = paymentEvent.orderName,
                totalAmount = paymentEvent.totalAmount(),
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

data class PaymentOrderInfo(
    val id: Long,
    val sellerId: Long,
    val amount: Long,
    val orderId: String
)
