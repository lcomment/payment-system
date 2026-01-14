package com.example.payment.monolith.payment.domain

import java.time.LocalDateTime

data class PaymentCancellationResult(
    val status: PaymentStatus,
    val canceledAt: LocalDateTime? = null,
    val cancelAmount: Long? = null,
    val failure: PaymentFailure? = null
) {
    init {
        if (status == PaymentStatus.FAILURE) {
            requireNotNull(failure) {
                "결제 취소 상태 FAILURE 일 때 PaymentFailure 는 null 값이 될 수 없습니다."
            }
        }
    }

    val message = when (status) {
        PaymentStatus.CANCELED -> "결제 취소에 성공하였습니다."
        PaymentStatus.FAILURE -> "결제 취소에 실패하였습니다."
        else -> error("현재 결제 취소 상태 (status: $status) 는 올바르지 않은 상태입니다.")
    }
}
