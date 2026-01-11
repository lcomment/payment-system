package com.example.payment.application.port.out

import com.example.payment.domain.PaymentEvent
import reactor.core.publisher.Mono

interface LoadPaymentPort {

  fun getPayment(orderId: String): Mono<PaymentEvent>
}
