package com.example.payment.monolith.payment.domain

import java.time.LocalDateTime

data class PaymentCancelExecutionResult(
    val paymentKey: String,
    val cancelAmount: Long,
    val canceledAt: LocalDateTime,
    val cancelReason: String,
    val pspRawData: String,
    val isSuccess: Boolean,
    val isFailure: Boolean,
    val failure: PaymentFailure? = null
) {
    init {
        require(isSuccess || isFailure) {
            "결제 취소 (paymentKey: $paymentKey) 는 올바르지 않은 취소 상태입니다."
        }
    }
}
