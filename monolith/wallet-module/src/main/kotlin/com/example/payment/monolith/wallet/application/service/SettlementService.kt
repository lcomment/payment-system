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
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

@UseCase
class SettlementService(
    private val loadWalletPort: LoadWalletPort,
    private val saveWalletPort: SaveWalletPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : SettlementUseCase {

    companion object {
        // Platform payout fee rate: 3.96%
        private val FEE_RATE = BigDecimal("0.0396")
    }

    @Transactional
    override fun processSettlement(event: PaymentConfirmedEvent) {
        processSettlementInternal(event)

        val walletSettledEvent = WalletSettledEvent(
            aggregateId = event.orderId,
            eventId = UUID.randomUUID().toString(),
            occurredAt = LocalDateTime.now(),
            orderId = event.orderId
        )

        inProcessEventBus.publish(walletSettledEvent)
        outboxPublisher.publish(walletSettledEvent)
    }

    /**
     * 오케스트레이터에서 호출 시 사용 - 이벤트 발행 없이 정산 처리만 수행
     * 단일 트랜잭션 내에서 Payment/Wallet/Ledger 처리 시 사용
     */
    @Transactional
    override fun processSettlementWithoutEvent(event: PaymentConfirmedEvent) {
        processSettlementInternal(event)
    }

    private fun processSettlementInternal(event: PaymentConfirmedEvent) {
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

        val updatedWallets = getUpdatedWallets(paymentOrdersBySellerId)

        saveWalletPort.save(updatedWallets)
    }

    private fun getUpdatedWallets(paymentOrdersBySellerId: Map<Long, List<PaymentOrder>>): List<Wallet> {
        val sellerIds = paymentOrdersBySellerId.keys

        val wallets = loadWalletPort.getWallets(sellerIds)

        return wallets.map { wallet ->
            wallet.calculateBalanceWith(paymentOrdersBySellerId[wallet.userId]!!)
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
