package com.example.payment.application.port.out

import com.example.payment.domain.PaymentEventMessage
import reactor.core.publisher.Flux

interface LoadPendingPaymentEventMessagePort {

  fun getPendingPaymentEventMessage(): Flux<PaymentEventMessage>
}
