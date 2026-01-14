package com.example.payment.monolith.payment.application.port.`in`

import com.example.payment.monolith.payment.domain.event.LedgerRecordedEvent
import com.example.payment.monolith.payment.domain.event.WalletSettledEvent

interface PaymentCompleteUseCase {
    fun completePayment(event: WalletSettledEvent)
    fun completePayment(event: LedgerRecordedEvent)
}
