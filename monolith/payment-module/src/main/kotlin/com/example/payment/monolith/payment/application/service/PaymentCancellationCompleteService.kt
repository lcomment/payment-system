package com.example.payment.monolith.payment.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancellationCompleteUseCase
import com.example.payment.monolith.payment.application.port.out.CompletePaymentPort
import com.example.payment.monolith.payment.application.port.out.LoadPaymentPort
import com.example.payment.monolith.payment.domain.event.LedgerReversedEvent
import com.example.payment.monolith.payment.domain.event.WalletRefundedEvent
import org.springframework.transaction.annotation.Transactional

@UseCase
class PaymentCancellationCompleteService(
    private val loadPaymentPort: LoadPaymentPort,
    private val completePaymentPort: CompletePaymentPort
) : PaymentCancellationCompleteUseCase {

    @Transactional
    override fun completeCancellation(event: WalletRefundedEvent) {
        val paymentEvent = loadPaymentPort.getPayment(event.orderId)

        // Confirm wallet reversal
        paymentEvent.confirmWalletReversal()

        // Check if both wallet and ledger reversals are done
        paymentEvent.completeCancellationIfDone()

        // Save updated state
        completePaymentPort.complete(paymentEvent)
    }

    @Transactional
    override fun completeCancellation(event: LedgerReversedEvent) {
        val paymentEvent = loadPaymentPort.getPayment(event.orderId)

        // Confirm ledger reversal
        paymentEvent.confirmLedgerReversal()

        // Check if both wallet and ledger reversals are done
        paymentEvent.completeCancellationIfDone()

        // Save updated state
        completePaymentPort.complete(paymentEvent)
    }
}
