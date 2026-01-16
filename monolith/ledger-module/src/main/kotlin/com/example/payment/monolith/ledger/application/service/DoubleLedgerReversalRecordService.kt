package com.example.payment.monolith.ledger.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.ledger.application.port.`in`.DoubleLedgerReversalRecordUseCase
import com.example.payment.monolith.ledger.application.port.out.LoadAccountPort
import com.example.payment.monolith.ledger.application.port.out.SaveDoubleLedgerEntryPort
import com.example.payment.monolith.ledger.domain.*
import com.example.payment.monolith.payment.domain.event.LedgerReversedEvent
import com.example.payment.monolith.payment.domain.event.PaymentCanceledEvent
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@UseCase
class DoubleLedgerReversalRecordService(
    private val loadAccountPort: LoadAccountPort,
    private val saveDoubleLedgerEntryPort: SaveDoubleLedgerEntryPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : DoubleLedgerReversalRecordUseCase {

    companion object {
        // Platform payout fee rate: 3.96%
        private val FEE_RATE = BigDecimal("0.0396")
    }

    @Transactional
    override fun recordReversalDoubleLedgerEntry(event: PaymentCanceledEvent) {
        val paymentAccountsForLedger = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PAYMENT_ORDER)
        val feeAccountsForLedger = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PLATFORM_FEE)

        // Create reversal ledger entries for each payment order
        val allReversalEntries = mutableListOf<DoubleLedgerEntry>()

        event.paymentOrders.forEach { orderInfo ->
            val grossAmount = orderInfo.amount
            val feeAmount = calculateFee(grossAmount)
            val netAmount = grossAmount - feeAmount

            // 1. Create reversal entry for net payment amount (reverse: Merchant -> Customer)
            val paymentOrderReversal = PaymentOrder(
                id = orderInfo.id,
                amount = netAmount,  // Net amount reversal
                orderId = orderInfo.orderId
            )
            allReversalEntries.addAll(
                Ledger.createReversalDoubleLedgerEntry(paymentAccountsForLedger, listOf(paymentOrderReversal))
            )

            // 2. Create reversal entry for platform fee (reverse: Revenue -> Customer)
            val platformFeeReversal = PlatformFee(
                id = orderInfo.id,  // Reference same payment order ID
                amount = feeAmount,  // Fee amount reversal
                orderId = orderInfo.orderId
            )
            allReversalEntries.addAll(
                Ledger.createReversalDoubleLedgerEntry(feeAccountsForLedger, listOf(platformFeeReversal))
            )
        }

        saveDoubleLedgerEntryPort.save(allReversalEntries)

        val ledgerReversedEvent = LedgerReversedEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(ledgerReversedEvent)
        outboxPublisher.publish(ledgerReversedEvent)
    }

    /**
     * Calculate platform fee: 3.96% of gross amount
     * Rounds up to ensure platform doesn't lose fractional amounts
     */
    private fun calculateFee(grossAmount: Long): Long {
        val feeDecimal = BigDecimal(grossAmount)
            .multiply(FEE_RATE)
            .setScale(0, RoundingMode.HALF_UP)
        return feeDecimal.toLong()
    }
}
