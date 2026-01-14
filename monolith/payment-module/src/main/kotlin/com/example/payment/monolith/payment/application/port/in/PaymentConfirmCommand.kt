package com.example.payment.monolith.payment.application.port.`in`

data class PaymentConfirmCommand(
    val paymentKey: String,
    val orderId: String,
    val amount: Long
)
