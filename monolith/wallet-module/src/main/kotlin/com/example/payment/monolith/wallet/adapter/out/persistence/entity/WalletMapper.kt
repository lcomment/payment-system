package com.example.payment.monolith.wallet.adapter.out.persistence.entity

import com.example.payment.monolith.wallet.domain.Wallet
import com.example.payment.monolith.wallet.domain.WalletTransaction
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class WalletMapper {

    fun mapToDomainEntity(entity: WalletEntity): Wallet {
        return Wallet(
            id = entity.id!!,
            userId = entity.userId,
            version = entity.version,
            balance = entity.balance,
            walletTransactions = emptyList()
        )
    }

    fun mapToJpaEntity(domain: Wallet): WalletEntity {
        return WalletEntity(
            id = domain.id,
            userId = domain.userId,
            balance = domain.balance,
            version = domain.version
        )
    }

    fun mapToJpaTransactionEntity(
        transaction: WalletTransaction,
        idempotencyKey: String
    ): WalletTransactionEntity {
        return WalletTransactionEntity(
            id = null,
            walletId = transaction.walletId,
            amount = BigDecimal(transaction.amount),
            type = transaction.type,
            orderId = transaction.orderId,
            referenceType = transaction.referenceType.name,
            referenceId = transaction.referenceId,
            idempotencyKey = idempotencyKey
        )
    }
}
