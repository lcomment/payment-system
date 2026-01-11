package com.example.ledger.application.port.out

import com.example.ledger.domain.DoubleLedgerEntry

interface SaveDoubleLedgerEntryPort {

  fun save(doubleLedgerEntries: List<DoubleLedgerEntry>)
}
