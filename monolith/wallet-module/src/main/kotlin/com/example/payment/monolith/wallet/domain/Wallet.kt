package com.example.payment.monolith.wallet.domain

import java.math.BigDecimal

data class Wallet(
    val id: Long,
    val userId: Long,
    val version: Int,
    val balance: BigDecimal,
    val walletTransactions: List<WalletTransaction> = emptyList()
) {

    fun calculateBalanceWith(items: List<Item>): Wallet {
        // Use netAmount (after fee deduction) for wallet balance calculation
        return copy(
            balance = balance + BigDecimal(items.sumOf { it.netAmount }),
            walletTransactions = items.map {
                WalletTransaction(
                    walletId = this.id,
                    amount = it.netAmount,  // Record net amount in wallet transaction
                    type = TransactionType.CREDIT,
                    referenceId = it.referenceId,
                    referenceType = it.referenceType,
                    orderId = it.orderId
                )
            }
        )
    }

    fun calculateBalanceWithRefund(items: List<Item>): Wallet {
        // Use netAmount for refund (same amount that was credited during settlement)
        return copy(
            balance = balance - BigDecimal(items.sumOf { it.netAmount }),
            walletTransactions = items.map {
                WalletTransaction(
                    walletId = this.id,
                    amount = it.netAmount,  // Refund the net amount
                    type = TransactionType.DEBIT,
                    referenceId = it.referenceId,
                    referenceType = it.referenceType,
                    orderId = it.orderId
                )
            }
        )
    }
}
