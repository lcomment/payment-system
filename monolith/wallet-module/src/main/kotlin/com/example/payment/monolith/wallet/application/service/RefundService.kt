package com.example.payment.monolith.wallet.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.payment.domain.event.PaymentCanceledEvent
import com.example.payment.monolith.wallet.application.port.`in`.RefundUseCase
import com.example.payment.monolith.wallet.application.port.out.LoadWalletPort
import com.example.payment.monolith.wallet.application.port.out.SaveWalletPort
import com.example.payment.monolith.wallet.domain.PaymentOrder
import com.example.payment.monolith.wallet.domain.Wallet
import com.example.payment.monolith.payment.domain.event.WalletRefundedEvent
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@UseCase
class RefundService(
    private val loadWalletPort: LoadWalletPort,
    private val saveWalletPort: SaveWalletPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : RefundUseCase {

    companion object {
        // Platform payout fee rate: 3.96%
        private val FEE_RATE = BigDecimal("0.0396")
    }

    @Transactional
    override fun processRefund(event: PaymentCanceledEvent) {
        // Convert event payment orders to domain payment orders with fee calculation
        val paymentOrders = event.paymentOrders.map { orderInfo ->
            val grossAmount = orderInfo.amount
            val feeAmount = calculateFee(grossAmount)
            val netAmount = grossAmount - feeAmount

            PaymentOrder(
                id = orderInfo.id,
                sellerId = orderInfo.sellerId,
                amount = grossAmount,
                feeAmount = feeAmount,
                netAmount = netAmount,
                orderId = orderInfo.orderId
            )
        }

        val paymentOrdersBySellerId = paymentOrders.groupBy { it.sellerId }

        val updatedWallets = getRefundedWallets(paymentOrdersBySellerId)

        saveWalletPort.save(updatedWallets)

        val walletRefundedEvent = WalletRefundedEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(walletRefundedEvent)
        outboxPublisher.publish(walletRefundedEvent)
    }

    private fun getRefundedWallets(paymentOrdersBySellerId: Map<Long, List<PaymentOrder>>): List<Wallet> {
        val sellerIds = paymentOrdersBySellerId.keys

        val wallets = loadWalletPort.getWallets(sellerIds)

        return wallets.map { wallet ->
            wallet.calculateBalanceWithRefund(paymentOrdersBySellerId[wallet.userId]!!)
        }
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
