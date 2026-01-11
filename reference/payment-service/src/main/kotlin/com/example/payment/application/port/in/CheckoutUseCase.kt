package com.example.payment.application.port.`in`

import com.example.payment.domain.CheckoutResult
import reactor.core.publisher.Mono

interface CheckoutUseCase {

  fun checkout(command: CheckoutCommand): Mono<CheckoutResult>
}
