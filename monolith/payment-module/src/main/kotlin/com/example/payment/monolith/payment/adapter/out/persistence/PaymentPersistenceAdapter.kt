package com.example.payment.monolith.payment.adapter.out.persistence

import com.example.payment.monolith.common.annotations.PersistenceAdapter
import com.example.payment.monolith.payment.adapter.out.persistence.entity.PaymentMapper
import com.example.payment.monolith.payment.adapter.out.persistence.repository.PaymentEventJpaRepository
import com.example.payment.monolith.payment.application.port.out.*
import com.example.payment.monolith.payment.domain.PaymentEvent
import com.example.payment.monolith.payment.domain.Product
import org.springframework.transaction.annotation.Transactional

@PersistenceAdapter
class PaymentPersistenceAdapter(
    private val paymentEventJpaRepository: PaymentEventJpaRepository,
    private val paymentMapper: PaymentMapper
) : SavePaymentPort,
    LoadPaymentPort,
    PaymentStatusUpdatePort,
    PaymentValidationPort,
    CompletePaymentPort,
    LoadProductPort {

    @Transactional
    override fun save(paymentEvent: PaymentEvent) {
        val entity = paymentMapper.mapToJpaEntity(paymentEvent)
        paymentEventJpaRepository.save(entity)
    }

    @Transactional(readOnly = true)
    override fun getPayment(orderId: String): PaymentEvent {
        val entity = paymentEventJpaRepository.findByOrderIdWithOrders(orderId)
            ?: throw IllegalArgumentException("Payment not found: $orderId")
        return paymentMapper.mapToDomainEntity(entity)
    }

    @Transactional(readOnly = true)
    override fun getPaymentByKey(paymentKey: String): PaymentEvent {
        val entity = paymentEventJpaRepository.findByPaymentKeyWithOrders(paymentKey)
            ?: throw IllegalArgumentException("Payment not found with key: $paymentKey")
        return paymentMapper.mapToDomainEntity(entity)
    }

    @Transactional
    override fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String) {
        val entity = paymentEventJpaRepository.findByOrderIdWithOrders(orderId)
            ?: throw IllegalArgumentException("Payment not found: $orderId")

        val updatedEntity = entity.copy(paymentKey = paymentKey)
        paymentEventJpaRepository.save(updatedEntity)
    }

    @Transactional
    override fun updatePaymentStatus(command: PaymentStatusUpdateCommand) {
        val entity = paymentEventJpaRepository.findByOrderIdWithOrders(command.orderId)
            ?: throw IllegalArgumentException("Payment not found: ${command.orderId}")

        val result = command.extraDetails
        val updatedEntity = entity.copy(
            paymentKey = entity.paymentKey,
            paymentType = entity.paymentType,
            paymentMethod = entity.paymentMethod,
            approvedAt = result?.approvedAt
        )

        paymentEventJpaRepository.save(updatedEntity)
    }

    @Transactional(readOnly = true)
    override fun isValid(orderId: String, amount: Long): Boolean {
        val entity = paymentEventJpaRepository.findByOrderIdWithOrders(orderId)
            ?: return false

        val totalAmount = entity.paymentOrders.sumOf { it.amount }
        return totalAmount == amount
    }

    @Transactional
    override fun complete(paymentEvent: PaymentEvent) {
        val entity = paymentMapper.mapToJpaEntity(paymentEvent)
        paymentEventJpaRepository.save(entity)
    }

    @Transactional(readOnly = true)
    override fun getProducts(cartId: Long, productIds: List<Long>): List<Product> {
        // TODO: Implement product loading from product service or database
        // For now, return mock data
        return productIds.map { productId ->
            Product(
                id = productId,
                name = "Product $productId",
                amount = 10000L,
                quantity = 1,
                sellerId = 1L
            )
        }
    }
}
