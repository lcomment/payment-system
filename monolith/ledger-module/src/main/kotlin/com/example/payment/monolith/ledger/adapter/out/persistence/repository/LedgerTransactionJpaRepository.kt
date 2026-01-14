package com.example.payment.monolith.ledger.adapter.out.persistence.repository

import com.example.payment.monolith.ledger.adapter.out.persistence.entity.LedgerTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LedgerTransactionJpaRepository : JpaRepository<LedgerTransactionEntity, Long> {

    fun existsByIdempotencyKey(idempotencyKey: String): Boolean
}
