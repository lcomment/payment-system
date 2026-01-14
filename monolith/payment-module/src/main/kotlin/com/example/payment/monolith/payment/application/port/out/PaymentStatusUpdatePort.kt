package com.example.payment.monolith.payment.application.port.out

interface PaymentStatusUpdatePort {
    fun updatePaymentStatusToExecuting(orderId: String, paymentKey: String)
    fun updatePaymentStatus(command: PaymentStatusUpdateCommand)
}
