package com.example.payment.monolith.ledger.application.port.out

import com.example.payment.monolith.ledger.domain.DoubleLedgerEntry

interface SaveDoubleLedgerEntryPort {
    fun save(entries: List<DoubleLedgerEntry>)
}
