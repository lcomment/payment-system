package com.example.payment.monolith.payment.adapter.`in`.web.request

data class TossPaymentCancelRequest(
    val paymentKey: String,
    val cancelReason: String
)
