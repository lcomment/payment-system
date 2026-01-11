package com.example.payment.adapter.out.web.toss.executor

import com.example.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.domain.PaymentExecutionResult
import reactor.core.publisher.Mono

interface PaymentExecutor {

  fun execute(command: PaymentConfirmCommand): Mono<PaymentExecutionResult>
}
