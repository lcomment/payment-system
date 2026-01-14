package com.example.payment.monolith.payment.application.port.out

interface PaymentValidationPort {
    fun isValid(orderId: String, amount: Long): Boolean
}
