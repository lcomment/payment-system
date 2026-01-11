package com.example.ledger.adapter.out.persistence

import com.example.common.PersistenceAdapter
import com.example.ledger.adapter.out.persistence.repository.LedgerEntryRepository
import com.example.ledger.adapter.out.persistence.repository.LedgerTransactionRepository
import com.example.ledger.application.port.out.DuplicateMessageFilterPort
import com.example.ledger.application.port.out.SaveDoubleLedgerEntryPort
import com.example.ledger.domain.DoubleLedgerEntry
import com.example.ledger.domain.PaymentEventMessage

@PersistenceAdapter
class LedgerPersistenceAdapter (
  private val ledgerTransactionRepository: LedgerTransactionRepository,
  private val ledgerEntryRepository: LedgerEntryRepository
) : DuplicateMessageFilterPort, SaveDoubleLedgerEntryPort {

  override fun isAlreadyProcess(message: PaymentEventMessage): Boolean {
    return ledgerTransactionRepository.isExist(message)
  }

  override fun save(doubleLedgerEntries: List<DoubleLedgerEntry>) {
    return ledgerEntryRepository.save(doubleLedgerEntries)
  }
}
