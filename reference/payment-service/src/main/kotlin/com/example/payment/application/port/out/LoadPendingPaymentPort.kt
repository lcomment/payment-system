package com.example.payment.application.port.out

import com.example.payment.domain.PendingPaymentEvent
import reactor.core.publisher.Flux

interface LoadPendingPaymentPort {

  fun getPendingPayments(): Flux<PendingPaymentEvent>
}
