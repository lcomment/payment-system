package com.example.payment.monolith.wallet.domain

data class WalletTransaction(
    val walletId: Long,
    val amount: Long,
    val type: TransactionType,
    val referenceId: Long,
    val referenceType: ReferenceType,
    val orderId: String
)
