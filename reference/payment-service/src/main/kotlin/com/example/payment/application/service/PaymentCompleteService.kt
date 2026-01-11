package com.example.payment.application.service

import com.example.common.UseCase
import com.example.payment.application.port.`in`.PaymentCompleteUseCase
import com.example.payment.application.port.out.CompletePaymentPort
import com.example.payment.application.port.out.LoadPaymentPort
import com.example.payment.domain.LedgerEventMessage
import com.example.payment.domain.WalletEventMessage
import reactor.core.publisher.Mono

@UseCase
class PaymentCompleteService (
  private val loadPaymentPort: LoadPaymentPort,
  private val completePaymentPort: CompletePaymentPort
) : PaymentCompleteUseCase {

  override fun completePayment(walletEventMessage: WalletEventMessage): Mono<Void> {
    return loadPaymentPort.getPayment(walletEventMessage.orderId())
      .map { it.apply { confirmWalletUpdate() } }
      .map { it.apply { completeIfDone() } }
      .flatMap { completePaymentPort.complete(it) }
  }

  override fun completePayment(ledgerEventMessage: LedgerEventMessage): Mono<Void> {
    return loadPaymentPort.getPayment(ledgerEventMessage.orderId())
      .map { it.apply { confirmLedgerUpdate() } }
      .map { it.apply { completeIfDone() } }
      .flatMap { completePaymentPort.complete(it) }
  }
}
