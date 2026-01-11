package com.example.payment.adapter.out.web.toss

import com.example.common.WebAdapter
import com.example.payment.adapter.out.web.toss.executor.PaymentExecutor
import com.example.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.application.port.out.PaymentExecutorPort
import com.example.payment.domain.PaymentExecutionResult
import reactor.core.publisher.Mono

@WebAdapter
class PaymentExecutorWebAdapter (
  private val paymentExecutor: PaymentExecutor
) : PaymentExecutorPort {

  override fun execute(command: PaymentConfirmCommand): Mono<PaymentExecutionResult> {
    return paymentExecutor.execute(command)
  }
}
