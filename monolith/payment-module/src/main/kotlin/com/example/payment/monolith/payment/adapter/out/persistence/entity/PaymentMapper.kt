package com.example.payment.monolith.payment.adapter.out.persistence.entity

import com.example.payment.monolith.payment.domain.PaymentEvent
import com.example.payment.monolith.payment.domain.PaymentOrder
import org.springframework.stereotype.Component

@Component
class PaymentMapper {

    fun mapToDomainEntity(entity: PaymentEventEntity): PaymentEvent {
        return PaymentEvent(
            id = entity.id,
            buyerId = entity.buyerId,
            orderName = entity.orderName,
            orderId = entity.orderId,
            paymentKey = entity.paymentKey,
            paymentType = entity.paymentType,
            paymentMethod = entity.paymentMethod,
            approvedAt = entity.approvedAt,
            paymentOrders = entity.paymentOrders.map { mapToDomainOrder(it) },
        )
    }

    private fun mapToDomainOrder(entity: PaymentOrderEntity): PaymentOrder {
        return PaymentOrder(
            id = entity.id,
            paymentEventId = entity.paymentEvent.id,
            sellerId = entity.sellerId,
            productId = entity.productId,
            orderId = entity.orderId,
            amount = entity.amount,
            paymentStatus = entity.paymentStatus,
        )
    }

    fun mapToJpaEntity(domain: PaymentEvent): PaymentEventEntity {
        val eventEntity = PaymentEventEntity(
            id = domain.id,
            buyerId = domain.buyerId,
            orderName = domain.orderName,
            orderId = domain.orderId,
            paymentKey = domain.paymentKey,
            paymentType = domain.paymentType,
            paymentMethod = domain.paymentMethod,
            approvedAt = domain.approvedAt,
            isPaymentDone = domain.isPaymentDone(),
            paymentOrders = mutableListOf()
        )

        val orderEntities = domain.paymentOrders.map { mapToJpaOrder(it, eventEntity) }
        eventEntity.paymentOrders.addAll(orderEntities)

        return eventEntity
    }

    private fun mapToJpaOrder(domain: PaymentOrder, eventEntity: PaymentEventEntity): PaymentOrderEntity {
        return PaymentOrderEntity(
            id = domain.id,
            paymentEvent = eventEntity,
            sellerId = domain.sellerId,
            productId = domain.productId,
            orderId = domain.orderId,
            amount = domain.amount,
            paymentStatus = domain.paymentStatus,
            isLedgerUpdated = domain.isLedgerUpdated(),
            isWalletUpdated = domain.isWalletUpdated()
        )
    }
}
