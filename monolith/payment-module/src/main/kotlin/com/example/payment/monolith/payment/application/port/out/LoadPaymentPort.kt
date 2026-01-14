package com.example.payment.monolith.payment.application.port.out

import com.example.payment.monolith.payment.domain.PaymentEvent

interface LoadPaymentPort {
    fun getPayment(orderId: String): PaymentEvent
    fun getPaymentByKey(paymentKey: String): PaymentEvent
}
