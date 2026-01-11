package com.example.ledger.adapter.out.persistence.repository

import com.example.ledger.domain.PaymentOrder

interface PaymentOrderRepository {

  fun getPaymentOrders(orderId: String): List<PaymentOrder>
}
