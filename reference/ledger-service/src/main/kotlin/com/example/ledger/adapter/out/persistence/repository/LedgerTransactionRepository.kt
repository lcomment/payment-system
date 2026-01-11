package com.example.ledger.adapter.out.persistence.repository

import com.example.ledger.domain.PaymentEventMessage

interface LedgerTransactionRepository {

  fun isExist(message: PaymentEventMessage): Boolean
}
