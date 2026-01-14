package com.example.payment.monolith.payment.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.payment.application.port.`in`.PaymentCompleteUseCase
import com.example.payment.monolith.payment.application.port.out.CompletePaymentPort
import com.example.payment.monolith.payment.application.port.out.LoadPaymentPort
import com.example.payment.monolith.payment.domain.event.LedgerRecordedEvent
import com.example.payment.monolith.payment.domain.event.WalletSettledEvent
import org.springframework.transaction.annotation.Transactional

@UseCase
class PaymentCompleteService(
    private val loadPaymentPort: LoadPaymentPort,
    private val completePaymentPort: CompletePaymentPort
) : PaymentCompleteUseCase {

    @Transactional
    override fun completePayment(event: WalletSettledEvent) {
        val paymentEvent = loadPaymentPort.getPayment(event.orderId)

        // Confirm wallet update
        paymentEvent.confirmWalletUpdate()

        // Check if both wallet and ledger are done
        paymentEvent.completeIfDone()

        // Save updated state
        completePaymentPort.complete(paymentEvent)
    }

    @Transactional
    override fun completePayment(event: LedgerRecordedEvent) {
        val paymentEvent = loadPaymentPort.getPayment(event.orderId)

        // Confirm ledger update
        paymentEvent.confirmLedgerUpdate()

        // Check if both wallet and ledger are done
        paymentEvent.completeIfDone()

        // Save updated state
        completePaymentPort.complete(paymentEvent)
    }
}
