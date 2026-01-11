package com.example.ledger.application.port.out

import com.example.ledger.domain.PaymentEventMessage

interface DuplicateMessageFilterPort {

  fun isAlreadyProcess(message: PaymentEventMessage): Boolean
}
