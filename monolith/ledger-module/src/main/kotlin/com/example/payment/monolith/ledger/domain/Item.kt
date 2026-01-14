package com.example.payment.monolith.ledger.domain

open class Item(
    val id: Long,
    val amount: Long,
    val orderId: String,
    val type: ReferenceType
)
