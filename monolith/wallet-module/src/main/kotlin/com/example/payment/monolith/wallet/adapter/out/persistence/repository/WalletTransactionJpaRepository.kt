package com.example.payment.monolith.wallet.adapter.out.persistence.repository

import com.example.payment.monolith.wallet.adapter.out.persistence.entity.WalletTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletTransactionJpaRepository : JpaRepository<WalletTransactionEntity, Long> {

    fun existsByIdempotencyKey(idempotencyKey: String): Boolean
}
