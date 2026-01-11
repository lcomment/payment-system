package com.example.wallet.adapter.out.persistence.entity

import com.example.common.IdempotencyCreator
import com.example.wallet.domain.WalletTransaction
import org.springframework.stereotype.Component

@Component
class JpaWalletTransactionMapper {

  fun mapToJpaEntity(walletTransaction: WalletTransaction): JpaWalletTransactionEntity {
    return JpaWalletTransactionEntity(
      walletId = walletTransaction.walletId,
      amount = walletTransaction.amount.toBigDecimal(),
      type = walletTransaction.type,
      referenceType = walletTransaction.referenceType.name,
      referenceId = walletTransaction.referenceId,
      orderId = walletTransaction.orderId,
      idempotencyKey = IdempotencyCreator.create(walletTransaction)
    )
  }
}
