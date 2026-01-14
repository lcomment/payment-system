package com.example.payment.monolith.ledger.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent

interface DoubleLedgerEntryRecordUseCase {
    fun recordDoubleLedgerEntry(event: PaymentConfirmedEvent)
}
