package com.example.ledger.application.port.out

import com.example.ledger.domain.PaymentOrder

interface LoadPaymentOrderPort {

  fun getPaymentOrders(orderId: String): List<PaymentOrder>
}
