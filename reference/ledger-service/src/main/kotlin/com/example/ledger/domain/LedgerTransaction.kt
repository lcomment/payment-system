package com.example.ledger.domain

data class LedgerTransaction (
  val referenceType: ReferenceType,
  val referenceId: Long,
  val orderId: String
)
