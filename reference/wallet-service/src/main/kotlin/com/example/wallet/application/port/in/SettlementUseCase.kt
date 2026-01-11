package com.example.wallet.application.port.`in`

import com.example.wallet.domain.PaymentEventMessage
import com.example.wallet.domain.WalletEventMessage

interface SettlementUseCase {

  fun processSettlement(paymentEventMessage: PaymentEventMessage): WalletEventMessage
}
