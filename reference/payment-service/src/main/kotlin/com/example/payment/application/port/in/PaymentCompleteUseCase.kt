package com.example.payment.application.port.`in`

import com.example.payment.domain.LedgerEventMessage
import com.example.payment.domain.WalletEventMessage
import reactor.core.publisher.Mono

interface PaymentCompleteUseCase {

  fun completePayment(walletEventMessage: WalletEventMessage): Mono<Void>

  fun completePayment(ledgerEventMessage: LedgerEventMessage): Mono<Void>
}
