package com.example.payment.monolith.ledger.domain

open class Item(
    val id: Long,
    val amount: Long,  // Amount for this specific item (could be gross, net, or fee amount)
    val orderId: String,
    val type: ReferenceType
)
