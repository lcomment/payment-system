package com.example.payment.monolith.payment.application.port.`in`

import com.example.payment.monolith.payment.domain.event.LedgerReversedEvent
import com.example.payment.monolith.payment.domain.event.WalletRefundedEvent

interface PaymentCancellationCompleteUseCase {
    fun completeCancellation(event: WalletRefundedEvent)
    fun completeCancellation(event: LedgerReversedEvent)
}
