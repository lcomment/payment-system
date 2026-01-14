package com.example.payment.monolith.payment.adapter.`in`.event

import com.example.payment.monolith.common.idempotency.IdempotencyChecker
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancellationCompleteUseCase
import com.example.payment.monolith.payment.application.port.`in`.PaymentCompleteUseCase
import com.example.payment.monolith.payment.domain.event.LedgerRecordedEvent
import com.example.payment.monolith.payment.domain.event.LedgerReversedEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class LedgerEventListener(
    private val paymentCompleteUseCase: PaymentCompleteUseCase,
    private val paymentCancellationCompleteUseCase: PaymentCancellationCompleteUseCase,
    private val idempotencyChecker: IdempotencyChecker
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onLedgerRecorded(event: LedgerRecordedEvent) {
        val alreadyProcessed = idempotencyChecker.checkAndRecord(
            key = "${event.eventId}-payment-complete",
            eventType = "LedgerRecordedEvent"
        )

        if (alreadyProcessed) {
            return
        }

        try {
            paymentCompleteUseCase.completePayment(event)
        } catch (e: Exception) {
            throw e
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onLedgerReversed(event: LedgerReversedEvent) {
        val alreadyProcessed = idempotencyChecker.checkAndRecord(
            key = "${event.eventId}-payment-cancellation-complete",
            eventType = "LedgerReversedEvent"
        )

        if (alreadyProcessed) {
            return
        }

        try {
            paymentCancellationCompleteUseCase.completeCancellation(event)
        } catch (e: Exception) {
            throw e
        }
    }
}
