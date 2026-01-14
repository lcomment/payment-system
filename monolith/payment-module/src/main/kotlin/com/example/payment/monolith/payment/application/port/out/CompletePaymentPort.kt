package com.example.payment.monolith.payment.application.port.out

import com.example.payment.monolith.payment.domain.PaymentEvent

interface CompletePaymentPort {
    fun complete(paymentEvent: PaymentEvent)
}
