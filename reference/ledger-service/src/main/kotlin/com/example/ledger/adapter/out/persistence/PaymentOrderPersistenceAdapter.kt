package com.example.ledger.adapter.out.persistence

import com.example.common.PersistenceAdapter
import com.example.ledger.adapter.out.persistence.repository.PaymentOrderRepository
import com.example.ledger.application.port.out.LoadPaymentOrderPort
import com.example.ledger.domain.PaymentOrder

@PersistenceAdapter
class PaymentOrderPersistenceAdapter (
  private val paymentOrderRepository: PaymentOrderRepository
) : LoadPaymentOrderPort {

  override fun getPaymentOrders(orderId: String): List<PaymentOrder> {
    return paymentOrderRepository.getPaymentOrders(orderId)
  }
}
