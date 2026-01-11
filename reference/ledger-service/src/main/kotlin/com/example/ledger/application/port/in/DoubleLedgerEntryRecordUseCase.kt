package com.example.ledger.application.port.`in`

import com.example.ledger.domain.LedgerEventMessage
import com.example.ledger.domain.PaymentEventMessage

interface DoubleLedgerEntryRecordUseCase {

  fun recordDoubleLedgerEntry(message: PaymentEventMessage): LedgerEventMessage
}
