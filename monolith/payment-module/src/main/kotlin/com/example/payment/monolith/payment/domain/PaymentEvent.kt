package com.example.payment.monolith.payment.domain

import java.time.LocalDateTime

data class PaymentEvent(
    val id: Long? = null,
    val buyerId: Long,
    val orderName: String,
    val orderId: String,
    val paymentKey: String? = null,
    val paymentType: PaymentType? = null,
    val paymentMethod: PaymentMethod? = null,
    val approvedAt: LocalDateTime? = null,
    val paymentOrders: List<PaymentOrder> = emptyList()
) {
    private var isPaymentDone: Boolean = false
    private var isCancellationDone: Boolean = false

    fun totalAmount(): Long {
        return paymentOrders.sumOf { it.amount }
    }

    fun isPaymentDone(): Boolean = isPaymentDone

    fun isSuccess(): Boolean {
        return paymentOrders.all { it.paymentStatus == PaymentStatus.SUCCESS }
    }

    fun isFailure(): Boolean {
        return paymentOrders.all { it.paymentStatus == PaymentStatus.FAILURE }
    }

    fun isUnknown(): Boolean {
        return paymentOrders.all { it.paymentStatus == PaymentStatus.UNKNOWN }
    }

    fun confirmWalletUpdate() {
        paymentOrders.forEach { it.confirmWalletUpdate() }
    }

    fun confirmLedgerUpdate() {
        paymentOrders.forEach { it.confirmLedgerUpdate() }
    }

    fun completeIfDone() {
        if (allPaymentOrdersDone()) {
            isPaymentDone = true
        }
    }

    fun isLedgerUpdateDone(): Boolean {
        return paymentOrders.all { it.isLedgerUpdated() }
    }

    fun isWalletUpdateDone(): Boolean {
        return paymentOrders.all { it.isWalletUpdated() }
    }

    private fun allPaymentOrdersDone(): Boolean {
        return paymentOrders.all { it.isWalletUpdated() && it.isLedgerUpdated() }
    }

    fun isCancellationDone(): Boolean = isCancellationDone

    fun confirmWalletReversal() {
        paymentOrders.forEach { it.confirmWalletReversal() }
    }

    fun confirmLedgerReversal() {
        paymentOrders.forEach { it.confirmLedgerReversal() }
    }

    fun completeCancellationIfDone() {
        if (allReversalsDone()) {
            isCancellationDone = true
        }
    }

    fun isLedgerReversalDone(): Boolean {
        return paymentOrders.all { it.isLedgerReversed() }
    }

    fun isWalletReversalDone(): Boolean {
        return paymentOrders.all { it.isWalletReversed() }
    }

    private fun allReversalsDone(): Boolean {
        return paymentOrders.all { it.isWalletReversed() && it.isLedgerReversed() }
    }
}
