package com.example.payment.monolith.payment.application.port.`in`

import com.example.payment.monolith.payment.domain.PaymentConfirmationResult

interface PaymentConfirmUseCase {
    fun confirm(command: PaymentConfirmCommand): PaymentConfirmationResult
}
