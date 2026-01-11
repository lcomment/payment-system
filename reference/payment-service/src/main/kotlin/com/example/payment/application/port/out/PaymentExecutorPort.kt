package com.example.payment.application.port.out

import com.example.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.domain.PaymentExecutionResult
import reactor.core.publisher.Mono

interface PaymentExecutorPort {

  fun execute(command: PaymentConfirmCommand): Mono<PaymentExecutionResult>
}
