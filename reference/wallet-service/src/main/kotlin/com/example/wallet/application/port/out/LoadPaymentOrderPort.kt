package com.example.wallet.application.port.out

import com.example.wallet.domain.PaymentOrder

interface LoadPaymentOrderPort {

  fun getPaymentOrders(orderId: String): List<PaymentOrder>
}
