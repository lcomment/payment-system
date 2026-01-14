package com.example.payment.monolith.payment.domain

data class CheckoutResult(
    val amount: Long,
    val orderId: String,
    val orderName: String
)
