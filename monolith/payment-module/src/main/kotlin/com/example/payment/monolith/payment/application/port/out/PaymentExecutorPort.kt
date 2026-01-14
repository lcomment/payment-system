package com.example.payment.monolith.payment.application.port.out

import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.monolith.payment.domain.PaymentExecutionResult

interface PaymentExecutorPort {
    fun execute(command: PaymentConfirmCommand): PaymentExecutionResult
}
