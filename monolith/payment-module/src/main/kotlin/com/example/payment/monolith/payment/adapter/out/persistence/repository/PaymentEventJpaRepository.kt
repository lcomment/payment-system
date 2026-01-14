package com.example.payment.monolith.payment.adapter.out.persistence.repository

import com.example.payment.monolith.payment.adapter.out.persistence.entity.PaymentEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentEventJpaRepository : JpaRepository<PaymentEventEntity, Long> {

    fun findByOrderId(orderId: String): PaymentEventEntity?

    @Query("SELECT pe FROM PaymentEventEntity pe LEFT JOIN FETCH pe.paymentOrders WHERE pe.orderId = :orderId")
    fun findByOrderIdWithOrders(orderId: String): PaymentEventEntity?
}
