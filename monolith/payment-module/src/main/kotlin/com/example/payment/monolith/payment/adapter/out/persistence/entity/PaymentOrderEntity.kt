package com.example.payment.monolith.payment.adapter.out.persistence.entity

import com.example.payment.monolith.payment.domain.PaymentStatus
import jakarta.persistence.*

@Entity
@Table(name = "payment_orders")
data class PaymentOrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_event_id", nullable = false)
    val paymentEvent: PaymentEventEntity,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(name = "order_id", nullable = false)
    val orderId: String,

    @Column(name = "amount", nullable = false)
    val amount: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    val paymentStatus: PaymentStatus,

    @Column(name = "is_ledger_updated", nullable = false)
    val isLedgerUpdated: Boolean = false,

    @Column(name = "is_wallet_updated", nullable = false)
    val isWalletUpdated: Boolean = false
)
