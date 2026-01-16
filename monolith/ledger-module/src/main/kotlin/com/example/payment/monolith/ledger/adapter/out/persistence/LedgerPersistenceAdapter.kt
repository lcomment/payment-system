package com.example.payment.monolith.ledger.adapter.out.persistence

import com.example.payment.monolith.common.annotations.PersistenceAdapter
import com.example.payment.monolith.ledger.adapter.out.persistence.entity.LedgerEntryEntity
import com.example.payment.monolith.ledger.adapter.out.persistence.entity.LedgerTransactionEntity
import com.example.payment.monolith.ledger.adapter.out.persistence.repository.AccountJpaRepository
import com.example.payment.monolith.ledger.adapter.out.persistence.repository.LedgerEntryJpaRepository
import com.example.payment.monolith.ledger.application.port.out.LoadAccountPort
import com.example.payment.monolith.ledger.application.port.out.SaveDoubleLedgerEntryPort
import com.example.payment.monolith.ledger.domain.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@PersistenceAdapter
class LedgerPersistenceAdapter(
    private val accountJpaRepository: AccountJpaRepository,
    private val ledgerEntryJpaRepository: LedgerEntryJpaRepository
) : LoadAccountPort, SaveDoubleLedgerEntryPort {

    @Transactional(readOnly = true)
    override fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger {
        return when (financeType) {
            FinanceType.PAYMENT_ORDER -> {
                // For payment orders: debit from customer account (1), credit to merchant account (2)
                val fromAccount = accountJpaRepository.findById(1L)
                    .orElseThrow { IllegalArgumentException("Customer account not found") }
                val toAccount = accountJpaRepository.findById(2L)
                    .orElseThrow { IllegalArgumentException("Merchant account not found") }

                DoubleAccountsForLedger(
                    from = Account(id = fromAccount.id!!, name = fromAccount.name),
                    to = Account(id = toAccount.id!!, name = toAccount.name)
                )
            }
            FinanceType.PLATFORM_FEE -> {
                // For platform fees: debit from customer account (1), credit to revenue account (3)
                val fromAccount = accountJpaRepository.findById(1L)
                    .orElseThrow { IllegalArgumentException("Customer account not found") }
                val toAccount = accountJpaRepository.findById(3L)
                    .orElseThrow { IllegalArgumentException("Revenue account not found") }

                DoubleAccountsForLedger(
                    from = Account(id = fromAccount.id!!, name = fromAccount.name),
                    to = Account(id = toAccount.id!!, name = toAccount.name)
                )
            }
        }
    }

    @Transactional
    override fun save(entries: List<DoubleLedgerEntry>) {
        entries.forEach { doubleLedgerEntry ->
            // Create transaction entity
            val transactionEntity = LedgerTransactionEntity(
                id = null,
                description = "Payment Order Settlement",
                referenceId = doubleLedgerEntry.transaction.referenceId,
                referenceType = doubleLedgerEntry.transaction.referenceType.name,
                orderId = doubleLedgerEntry.transaction.orderId,
                idempotencyKey = "${doubleLedgerEntry.transaction.orderId}-${doubleLedgerEntry.transaction.referenceId}"
            )

            // Create credit entry
            val creditEntry = LedgerEntryEntity(
                id = null,
                amount = BigDecimal(doubleLedgerEntry.credit.amount),
                accountId = doubleLedgerEntry.credit.account.id,
                transaction = transactionEntity,
                type = doubleLedgerEntry.credit.type
            )

            // Create debit entry
            val debitEntry = LedgerEntryEntity(
                id = null,
                amount = BigDecimal(doubleLedgerEntry.debit.amount),
                accountId = doubleLedgerEntry.debit.account.id,
                transaction = transactionEntity,
                type = doubleLedgerEntry.debit.type
            )

            // Save entries (transaction will be cascaded)
            ledgerEntryJpaRepository.save(creditEntry)
            ledgerEntryJpaRepository.save(debitEntry)
        }
    }
}
