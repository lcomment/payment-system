package com.example.ledger.adapter.out.persistence.entity

import com.example.common.IdempotencyCreator
import com.example.ledger.domain.LedgerTransaction
import org.springframework.stereotype.Component

@Component
class JpaLedgerTransactionMapper {

  fun mapToJpaEntity(ledgerTransaction: LedgerTransaction): JpaLedgerTransactionEntity {
    return JpaLedgerTransactionEntity(
      description = "LedgerService record transaction",
      referenceType = ledgerTransaction.referenceType.name,
      referenceId = ledgerTransaction.referenceId,
      orderId = ledgerTransaction.orderId,
      idempotencyKey = IdempotencyCreator.create(ledgerTransaction)
    )
  }
}
