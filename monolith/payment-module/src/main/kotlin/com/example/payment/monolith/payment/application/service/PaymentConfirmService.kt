package com.example.payment.monolith.payment.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmUseCase
import com.example.payment.monolith.payment.application.port.out.*
import com.example.payment.monolith.payment.domain.PaymentConfirmationResult
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import org.springframework.transaction.annotation.Transactional

@UseCase
class PaymentConfirmService(
    private val paymentStatusUpdatePort: PaymentStatusUpdatePort,
    private val paymentValidationPort: PaymentValidationPort,
    private val paymentExecutorPort: PaymentExecutorPort,
    private val loadPaymentPort: LoadPaymentPort,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : PaymentConfirmUseCase {

    @Transactional
    override fun confirm(command: PaymentConfirmCommand): PaymentConfirmationResult {
        // 1. Update status to EXECUTING
        paymentStatusUpdatePort.updatePaymentStatusToExecuting(command.orderId, command.paymentKey)

        // 2. Validate amount
        val isValid = paymentValidationPort.isValid(command.orderId, command.amount)
        if (!isValid) {
            throw IllegalArgumentException("결제 금액이 올바르지 않습니다.")
        }

        // 3. Execute payment via PSP (Toss)
        val executionResult = paymentExecutorPort.execute(command)

        // 4. Update payment status based on execution result
        paymentStatusUpdatePort.updatePaymentStatus(
            PaymentStatusUpdateCommand(executionResult)
        )

        // 5. If success, publish event
        if (executionResult.isSuccess) {
            val paymentEvent = loadPaymentPort.getPayment(command.orderId)
            val event = PaymentConfirmedEvent.from(paymentEvent)

            // Publish to in-process event bus (synchronous within transaction)
            inProcessEventBus.publish(event)

            // Persist to outbox for SQS (same transaction)
            outboxPublisher.publish(event)
        }

        return PaymentConfirmationResult(
            status = executionResult.paymentStatus(),
            failure = executionResult.failure
        )
    }
}
