package com.example.payment.monolith.ledger.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentCanceledEvent

interface DoubleLedgerReversalRecordUseCase {
    fun recordReversalDoubleLedgerEntry(event: PaymentCanceledEvent)
}
