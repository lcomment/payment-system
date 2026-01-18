package com.example.payment.monolith.payment.domain

data class PaymentOrder(
    val id: Long? = null,
    val paymentEventId: Long? = null,
    val sellerId: Long,
    val productId: Long,
    val orderId: String,
    val amount: Long,
    val paymentStatus: PaymentStatus,
) {
    private var isLedgerUpdated: Boolean = false
    private var isWalletUpdated: Boolean = false
    private var isLedgerReversed: Boolean = false
    private var isWalletReversed: Boolean = false

    fun isLedgerUpdated(): Boolean = isLedgerUpdated

    fun isWalletUpdated(): Boolean = isWalletUpdated

    fun confirmWalletUpdate() {
        isWalletUpdated = true
    }

    fun confirmLedgerUpdate() {
        isLedgerUpdated = true
    }

    fun isLedgerReversed(): Boolean = isLedgerReversed

    fun isWalletReversed(): Boolean = isWalletReversed

    fun confirmWalletReversal() {
        isWalletReversed = true
    }

    fun confirmLedgerReversal() {
        isLedgerReversed = true
    }

    fun restoreUpdateFlags(
        ledgerUpdated: Boolean,
        walletUpdated: Boolean,
        ledgerReversed: Boolean,
        walletReversed: Boolean
    ) {
        isLedgerUpdated = ledgerUpdated
        isWalletUpdated = walletUpdated
        isLedgerReversed = ledgerReversed
        isWalletReversed = walletReversed
    }
}
