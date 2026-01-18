package com.example.payment.monolith.ledger.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.ledger.application.port.`in`.DoubleLedgerEntryRecordUseCase
import com.example.payment.monolith.ledger.application.port.out.LoadAccountPort
import com.example.payment.monolith.ledger.application.port.out.SaveDoubleLedgerEntryPort
import com.example.payment.monolith.ledger.domain.*
import com.example.payment.monolith.ledger.domain.event.LedgerRecordedEvent
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@UseCase
class DoubleLedgerEntryRecordService(
    private val loadAccountPort: LoadAccountPort,
    private val saveDoubleLedgerEntryPort: SaveDoubleLedgerEntryPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : DoubleLedgerEntryRecordUseCase {

    companion object {
        // Platform payout fee rate: 3.96%
        private val FEE_RATE = BigDecimal("0.0396")
    }

    @Transactional
    override fun recordDoubleLedgerEntry(event: PaymentConfirmedEvent) {
        recordDoubleLedgerEntryInternal(event)

        val ledgerRecordedEvent = LedgerRecordedEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(ledgerRecordedEvent)
        outboxPublisher.publish(ledgerRecordedEvent)
    }

    /**
     * 오케스트레이터에서 호출 시 사용 - 이벤트 발행 없이 원장 기록만 수행
     * 단일 트랜잭션 내에서 Payment/Wallet/Ledger 처리 시 사용
     */
    @Transactional
    override fun recordDoubleLedgerEntryWithoutEvent(event: PaymentConfirmedEvent) {
        recordDoubleLedgerEntryInternal(event)
    }

    private fun recordDoubleLedgerEntryInternal(event: PaymentConfirmedEvent) {
        val paymentAccountsForLedger = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PAYMENT_ORDER)
        val feeAccountsForLedger = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PLATFORM_FEE)

        // Create ledger entries for each payment order
        val allLedgerEntries = mutableListOf<DoubleLedgerEntry>()

        event.paymentOrders.forEach { orderInfo ->
            val grossAmount = orderInfo.amount
            val feeAmount = calculateFee(grossAmount)
            val netAmount = grossAmount - feeAmount

            // 1. Create ledger entry for net payment amount (Customer -> Merchant)
            val paymentOrderEntry = PaymentOrder(
                id = orderInfo.id,
                amount = netAmount,  // Net amount to merchant
                orderId = orderInfo.orderId
            )
            allLedgerEntries.addAll(
                Ledger.createDoubleLedgerEntry(paymentAccountsForLedger, listOf(paymentOrderEntry))
            )

            // 2. Create ledger entry for platform fee (Customer -> Revenue)
            val platformFeeEntry = PlatformFee(
                id = orderInfo.id,  // Reference same payment order ID
                amount = feeAmount,  // Fee amount to platform
                orderId = orderInfo.orderId
            )
            allLedgerEntries.addAll(
                Ledger.createDoubleLedgerEntry(feeAccountsForLedger, listOf(platformFeeEntry))
            )
        }

        saveDoubleLedgerEntryPort.save(allLedgerEntries)
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
