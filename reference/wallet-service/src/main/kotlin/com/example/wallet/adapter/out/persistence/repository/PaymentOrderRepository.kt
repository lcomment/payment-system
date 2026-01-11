package com.example.wallet.adapter.out.persistence.repository

import com.example.wallet.domain.PaymentOrder

interface PaymentOrderRepository {

  fun getPaymentOrders(orderId: String): List<PaymentOrder>
}
