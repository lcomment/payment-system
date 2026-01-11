package com.example.payment.application.port.out

import com.example.payment.domain.PaymentEvent
import reactor.core.publisher.Mono

interface CompletePaymentPort {

  fun complete(paymentEvent: PaymentEvent): Mono<Void>
}
