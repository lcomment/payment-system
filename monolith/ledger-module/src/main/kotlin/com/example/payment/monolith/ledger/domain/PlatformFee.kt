package com.example.payment.monolith.ledger.domain

class PlatformFee(
    id: Long,  // Reference to the payment order ID
    amount: Long,  // Fee amount (3.96% of payment order)
    orderId: String
) : Item(
    id = id,
    amount = amount,
    orderId = orderId,
    type = ReferenceType.PLATFORM_FEE
)
