package com.example.payment.monolith.wallet.domain

open class Item(
    val amount: Long,
    val orderId: String,
    val referenceId: Long,
    val referenceType: ReferenceType
)

enum class ReferenceType {
    PAYMENT_ORDER
}
