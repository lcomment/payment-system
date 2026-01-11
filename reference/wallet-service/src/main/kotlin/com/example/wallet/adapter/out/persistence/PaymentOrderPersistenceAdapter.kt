package com.example.wallet.adapter.out.persistence

import com.example.common.PersistenceAdapter
import com.example.wallet.adapter.out.persistence.repository.PaymentOrderRepository
import com.example.wallet.application.port.out.LoadPaymentOrderPort
import com.example.wallet.domain.PaymentOrder

@PersistenceAdapter
class PaymentOrderPersistenceAdapter (
  private val paymentOrderRepository: PaymentOrderRepository
) : LoadPaymentOrderPort {

  override fun getPaymentOrders(orderId: String): List<PaymentOrder> {
    return paymentOrderRepository.getPaymentOrders(orderId)
  }
}
