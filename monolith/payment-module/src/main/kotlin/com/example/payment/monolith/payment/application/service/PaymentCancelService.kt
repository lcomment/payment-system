package com.example.payment.monolith.payment.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.event.InProcessEventBus
import com.example.payment.monolith.common.outbox.OutboxPublisher
import com.example.payment.monolith.payment.adapter.out.web.toss.executor.TossPaymentExecutor
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelUseCase
import com.example.payment.monolith.payment.application.port.out.LoadPaymentPort
import com.example.payment.monolith.payment.application.port.out.SavePaymentPort
import com.example.payment.monolith.payment.domain.PaymentCancellationResult
import com.example.payment.monolith.payment.domain.PaymentStatus
import com.example.payment.monolith.payment.domain.event.PaymentCanceledEvent
import org.springframework.transaction.annotation.Transactional

@UseCase
class PaymentCancelService(
    private val loadPaymentPort: LoadPaymentPort,
    private val savePaymentPort: SavePaymentPort,
    private val tossPaymentExecutor: TossPaymentExecutor,
    private val inProcessEventBus: InProcessEventBus,
    private val outboxPublisher: OutboxPublisher
) : PaymentCancelUseCase {

    @Transactional
    override fun cancel(command: PaymentCancelCommand): PaymentCancellationResult {
        // 1. Load payment by paymentKey
        val paymentEvent = try {
            loadPaymentPort.getPaymentByKey(command.paymentKey)
        } catch (e: Exception) {
            return PaymentCancellationResult(
                status = PaymentStatus.FAILURE,
                failure = com.example.payment.monolith.payment.domain.PaymentFailure(
                    errorCode = "PAYMENT_NOT_FOUND",
                    message = "결제 정보를 찾을 수 없습니다."
                )
            )
        }

        // 2. Validate: payment must be SUCCESS
        if (!paymentEvent.isSuccess()) {
            return PaymentCancellationResult(
                status = PaymentStatus.FAILURE,
                failure = com.example.payment.monolith.payment.domain.PaymentFailure(
                    errorCode = "INVALID_PAYMENT_STATUS",
                    message = "취소 가능한 결제 상태가 아닙니다. (현재 상태: ${paymentEvent.paymentOrders.firstOrNull()?.paymentStatus})"
                )
            )
        }

        // 3. Validate: payment must be completed (wallet and ledger updates done)
        if (!paymentEvent.isPaymentDone()) {
            return PaymentCancellationResult(
                status = PaymentStatus.FAILURE,
                failure = com.example.payment.monolith.payment.domain.PaymentFailure(
                    errorCode = "PAYMENT_NOT_COMPLETED",
                    message = "결제가 완료되지 않아 취소할 수 없습니다."
                )
            )
        }

        // 4. Execute cancellation via PSP (Toss)
        val cancelResult = tossPaymentExecutor.executeCancel(command.paymentKey, command.cancelReason)

        if (!cancelResult.isSuccess) {
            return PaymentCancellationResult(
                status = PaymentStatus.FAILURE,
                failure = cancelResult.failure
            )
        }

        // 5. Save payment event (will be updated via mapper with CANCELED status)
        savePaymentPort.save(paymentEvent)

        // 6. Publish PaymentCanceledEvent
        val event = PaymentCanceledEvent.from(paymentEvent, command.cancelReason)

        // Publish to in-process event bus (synchronous within transaction)
        inProcessEventBus.publish(event)

        // Persist to outbox for SQS (same transaction)
        outboxPublisher.publish(event)

        return PaymentCancellationResult(
            status = PaymentStatus.CANCELED,
            canceledAt = cancelResult.canceledAt,
            cancelAmount = cancelResult.cancelAmount
        )
    }
}
