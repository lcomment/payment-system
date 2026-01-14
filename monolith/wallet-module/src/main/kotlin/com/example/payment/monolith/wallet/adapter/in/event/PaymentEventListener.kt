package com.example.payment.monolith.wallet.adapter.`in`.event

import com.example.payment.monolith.common.idempotency.IdempotencyChecker
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import com.example.payment.monolith.wallet.application.port.`in`.SettlementUseCase
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val settlementUseCase: SettlementUseCase,
    private val idempotencyChecker: IdempotencyChecker
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onPaymentConfirmed(event: PaymentConfirmedEvent) {
        val alreadyProcessed = idempotencyChecker.checkAndRecord(
            key = "${event.eventId}-wallet-settlement",
            eventType = "PaymentConfirmedEvent"
        )

        if (alreadyProcessed) {
            return
        }

        try {
            settlementUseCase.processSettlement(event)
        } catch (e: Exception) {
            throw e
        }
    }
}
