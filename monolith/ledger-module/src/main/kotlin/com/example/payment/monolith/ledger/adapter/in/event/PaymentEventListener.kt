package com.example.payment.monolith.ledger.adapter.`in`.event

import com.example.payment.monolith.common.idempotency.IdempotencyChecker
import com.example.payment.monolith.ledger.application.port.`in`.DoubleLedgerEntryRecordUseCase
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val doubleLedgerEntryRecordUseCase: DoubleLedgerEntryRecordUseCase,
    private val idempotencyChecker: IdempotencyChecker
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onPaymentConfirmed(event: PaymentConfirmedEvent) {

        val alreadyProcessed = idempotencyChecker.checkAndRecord(
            key = "${event.eventId}-ledger-record",
            eventType = "PaymentConfirmedEvent"
        )

        if (alreadyProcessed) {
            return
        }

        try {
            doubleLedgerEntryRecordUseCase.recordDoubleLedgerEntry(event)
        } catch (e: Exception) {
            throw e
        }
    }
}
