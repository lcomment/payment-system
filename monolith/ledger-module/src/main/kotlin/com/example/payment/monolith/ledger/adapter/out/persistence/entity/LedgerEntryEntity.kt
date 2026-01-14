package com.example.payment.monolith.ledger.adapter.out.persistence.entity

import com.example.payment.monolith.ledger.domain.LedgerEntryType
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "ledger_entries")
class LedgerEntryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val amount: BigDecimal,

    @Column(name = "account_id")
    val accountId: Long,

    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val transaction: LedgerTransactionEntity,

    @Enumerated(EnumType.STRING)
    val type: LedgerEntryType
)
