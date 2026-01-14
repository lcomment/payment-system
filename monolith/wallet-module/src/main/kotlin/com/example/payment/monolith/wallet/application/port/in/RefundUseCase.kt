package com.example.payment.monolith.wallet.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentCanceledEvent

interface RefundUseCase {
    fun processRefund(event: PaymentCanceledEvent)
}
