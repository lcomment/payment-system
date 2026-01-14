package com.example.payment.monolith.wallet.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent

interface SettlementUseCase {
    fun processSettlement(event: PaymentConfirmedEvent)
}
