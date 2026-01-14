package com.example.payment.monolith.payment.application.port.out

import com.example.payment.monolith.payment.domain.PaymentEvent

interface SavePaymentPort {
    fun save(paymentEvent: PaymentEvent)
}
