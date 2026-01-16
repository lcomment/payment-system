package com.example.payment.monolith.wallet.domain

class PaymentOrder(
    val id: Long,
    val sellerId: Long,
    amount: Long,
    feeAmount: Long,
    netAmount: Long,
    orderId: String
) : Item(
    amount = amount,
    feeAmount = feeAmount,
    netAmount = netAmount,
    orderId = orderId,
    referenceId = id,
    referenceType = ReferenceType.PAYMENT_ORDER
)
