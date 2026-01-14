package com.example.payment.monolith.payment.application.port.`in`

import com.example.payment.monolith.payment.domain.PaymentCancellationResult

interface PaymentCancelUseCase {
    fun cancel(command: PaymentCancelCommand): PaymentCancellationResult
}
