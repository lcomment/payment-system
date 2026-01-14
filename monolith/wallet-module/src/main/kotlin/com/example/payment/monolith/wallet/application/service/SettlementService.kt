package com.example.payment.monolith.wallet.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import com.example.payment.monolith.wallet.application.port.`in`.SettlementUseCase
import com.example.payment.monolith.wallet.application.port.out.LoadWalletPort
import com.example.payment.monolith.wallet.application.port.out.SaveWalletPort
import com.example.payment.monolith.wallet.domain.PaymentOrder
import com.example.payment.monolith.wallet.domain.Wallet
import com.example.payment.monolith.wallet.domain.event.WalletSettledEvent
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@UseCase
class SettlementService(
    private val loadWalletPort: LoadWalletPort,
    private val saveWalletPort: SaveWalletPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : SettlementUseCase {

    @Transactional
    override fun processSettlement(event: PaymentConfirmedEvent) {
        // Convert event payment orders to domain payment orders
        val paymentOrders = event.paymentOrders.map { orderInfo ->
            PaymentOrder(
                id = orderInfo.id,
                sellerId = orderInfo.sellerId,
                amount = orderInfo.amount,
                orderId = orderInfo.orderId
            )
        }

        val paymentOrdersBySellerId = paymentOrders.groupBy { it.sellerId }

        val updatedWallets = getUpdatedWallets(paymentOrdersBySellerId)

        saveWalletPort.save(updatedWallets)

        val walletSettledEvent = WalletSettledEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(walletSettledEvent)
        outboxPublisher.publish(walletSettledEvent)
    }

    private fun getUpdatedWallets(paymentOrdersBySellerId: Map<Long, List<PaymentOrder>>): List<Wallet> {
        val sellerIds = paymentOrdersBySellerId.keys

        val wallets = loadWalletPort.getWallets(sellerIds)

        return wallets.map { wallet ->
            wallet.calculateBalanceWith(paymentOrdersBySellerId[wallet.userId]!!)
        }
    }
}
