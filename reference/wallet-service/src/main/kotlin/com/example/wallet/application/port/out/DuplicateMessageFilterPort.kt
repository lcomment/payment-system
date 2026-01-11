package com.example.wallet.application.port.out

import com.example.wallet.domain.PaymentEventMessage

interface DuplicateMessageFilterPort {

  fun isAlreadyProcess(paymentEventMessage: PaymentEventMessage): Boolean
}
