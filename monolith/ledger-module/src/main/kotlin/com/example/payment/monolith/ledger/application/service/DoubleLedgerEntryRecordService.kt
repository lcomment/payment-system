package com.example.payment.monolith.ledger.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.ledger.application.port.`in`.DoubleLedgerEntryRecordUseCase
import com.example.payment.monolith.ledger.application.port.out.LoadAccountPort
import com.example.payment.monolith.ledger.application.port.out.SaveDoubleLedgerEntryPort
import com.example.payment.monolith.ledger.domain.FinanceType
import com.example.payment.monolith.ledger.domain.Ledger
import com.example.payment.monolith.ledger.domain.PaymentOrder
import com.example.payment.monolith.ledger.domain.event.LedgerRecordedEvent
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@UseCase
class DoubleLedgerEntryRecordService(
    private val loadAccountPort: LoadAccountPort,
    private val saveDoubleLedgerEntryPort: SaveDoubleLedgerEntryPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : DoubleLedgerEntryRecordUseCase {

    @Transactional
    override fun recordDoubleLedgerEntry(event: PaymentConfirmedEvent) {
        val doubleAccountsForLedger = loadAccountPort.getDoubleAccountsForLedger(FinanceType.PAYMENT_ORDER)

        // Convert event payment orders to domain payment orders
        val paymentOrders = event.paymentOrders.map { orderInfo ->
            PaymentOrder(
                id = orderInfo.id,
                amount = orderInfo.amount,
                orderId = orderInfo.orderId
            )
        }

        val doubleLedgerEntries = Ledger.createDoubleLedgerEntry(doubleAccountsForLedger, paymentOrders)

        saveDoubleLedgerEntryPort.save(doubleLedgerEntries)

        val ledgerRecordedEvent = LedgerRecordedEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(ledgerRecordedEvent)
        outboxPublisher.publish(ledgerRecordedEvent)
    }
}
