package com.example.payment.monolith.ledger.domain

class Ledger {

    companion object {

        fun createDoubleLedgerEntry(doubleAccountsForLedger: DoubleAccountsForLedger, items: List<Item>): List<DoubleLedgerEntry> {
            return items.map { item ->
                DoubleLedgerEntry(
                    credit = LedgerEntry(
                        account = doubleAccountsForLedger.to,
                        amount = item.amount,
                        type = LedgerEntryType.CREDIT
                    ),
                    debit = LedgerEntry(
                        account = doubleAccountsForLedger.from,
                        amount = item.amount,
                        type = LedgerEntryType.DEBIT
                    ),
                    transaction = LedgerTransaction(
                        referenceType = item.type,
                        referenceId = item.id,
                        orderId = item.orderId
                    )
                )
            }
        }

        fun createReversalDoubleLedgerEntry(doubleAccountsForLedger: DoubleAccountsForLedger, items: List<Item>): List<DoubleLedgerEntry> {
            return items.map { item ->
                DoubleLedgerEntry(
                    // Reversal: swap credit and debit from original entry
                    credit = LedgerEntry(
                        account = doubleAccountsForLedger.from,  // Swapped: was debit, now credit
                        amount = item.amount,
                        type = LedgerEntryType.CREDIT
                    ),
                    debit = LedgerEntry(
                        account = doubleAccountsForLedger.to,  // Swapped: was credit, now debit
                        amount = item.amount,
                        type = LedgerEntryType.DEBIT
                    ),
                    transaction = LedgerTransaction(
                        referenceType = item.type,
                        referenceId = item.id,
                        orderId = item.orderId
                    )
                )
            }
        }
    }
}
