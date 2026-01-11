package com.example.payment.application.port.out

import com.example.payment.domain.PaymentEventMessage

interface DispatchEventMessagePort {

  fun dispatch(paymentEventMessage: PaymentEventMessage)
}
