package com.example.payment.monolith.ledger.domain

data class LedgerTransaction(
    val referenceType: ReferenceType,
    val referenceId: Long,
    val orderId: String
)

enum class ReferenceType {
    PAYMENT_ORDER,
    PLATFORM_FEE
}
