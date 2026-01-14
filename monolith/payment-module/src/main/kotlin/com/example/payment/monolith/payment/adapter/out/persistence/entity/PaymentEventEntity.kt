package com.example.payment.monolith.payment.adapter.out.persistence.entity

import com.example.payment.monolith.payment.domain.PaymentMethod
import com.example.payment.monolith.payment.domain.PaymentType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment_events")
data class PaymentEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,

    @Column(name = "order_name", nullable = false)
    val orderName: String,

    @Column(name = "order_id", unique = true, nullable = false)
    val orderId: String,

    @Column(name = "payment_key")
    val paymentKey: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    val paymentType: PaymentType? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    val paymentMethod: PaymentMethod? = null,

    @Column(name = "approved_at")
    val approvedAt: LocalDateTime? = null,

    @Column(name = "is_payment_done", nullable = false)
    val isPaymentDone: Boolean = false,

    @OneToMany(
        mappedBy = "paymentEvent",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val paymentOrders: MutableList<PaymentOrderEntity> = mutableListOf()
)
