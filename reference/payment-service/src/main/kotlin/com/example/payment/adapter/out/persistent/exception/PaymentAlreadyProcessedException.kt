package com.example.payment.adapter.out.persistent.exception

import com.example.payment.domain.PaymentStatus

class PaymentAlreadyProcessedException(
  val status: PaymentStatus,
  message: String
) : RuntimeException(message) {
}
