package com.example.payment.monolith.payment.domain

data class PaymentFailure(
    val errorCode: String,
    val message: String
)
