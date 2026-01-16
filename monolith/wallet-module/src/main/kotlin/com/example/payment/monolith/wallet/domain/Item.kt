package com.example.payment.monolith.wallet.domain

open class Item(
    val amount: Long,  // Gross amount (original payment amount)
    val feeAmount: Long = 0,  // Platform fee (3.96% of amount)
    val netAmount: Long = amount,  // Net amount after fee deduction (amount - feeAmount)
    val orderId: String,
    val referenceId: Long,
    val referenceType: ReferenceType
)

enum class ReferenceType {
    PAYMENT_ORDER
}
