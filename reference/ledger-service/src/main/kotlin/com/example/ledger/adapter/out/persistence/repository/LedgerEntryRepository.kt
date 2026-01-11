package com.example.ledger.adapter.out.persistence.repository

import com.example.ledger.domain.DoubleLedgerEntry

interface LedgerEntryRepository {

  fun save(doubleLedgerEntries: List<DoubleLedgerEntry>)
}
