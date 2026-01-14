package com.example.payment.monolith.payment.application.port.`in`

data class PaymentCancelCommand(
    val paymentKey: String,
    val cancelReason: String
)
