package com.example.payment.monolith.wallet.adapter.out.persistence.entity

import com.example.payment.monolith.wallet.domain.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "wallet_transactions")
class WalletTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "wallet_id", nullable = false)
    val walletId: Long,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val type: TransactionType,

    @Column(name = "order_id", nullable = false)
    val orderId: String,

    @Column(name = "reference_type", nullable = false)
    val referenceType: String,

    @Column(name = "reference_id", nullable = false)
    val referenceId: Long,

    @Column(name = "idempotency_key", nullable = false)
    val idempotencyKey: String
)
