package com.example.payment.monolith.payment.adapter.`in`.sqs

import com.example.payment.monolith.common.event.CancelPaymentCommand
import com.example.payment.monolith.common.idempotency.IdempotencyChecker
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelUseCase
import com.example.payment.monolith.payment.domain.PaymentStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 * PaymentCancelSqsListener - payment-cancel-queue에서 취소 요청을 수신하여 PG 취소 수행
 *
 * 문서 정책:
 * - Fulfillment에서 지급 실패 시 Outbox를 통해 CANCEL 요청이 큐로 전송됨
 * - 이 리스너가 수신하여 PG 취소를 수행하고 Payment 상태를 CANCELED로 갱신
 * - 멱등성: 이미 CANCELED된 결제는 성공 처리 (중복 취소 안전)
 */
@Component
@ConditionalOnProperty(name = ["cloud.aws.sqs.enabled"], havingValue = "true", matchIfMissing = false)
class PaymentCancelSqsListener(
    private val paymentCancelUseCase: PaymentCancelUseCase,
    private val idempotencyChecker: IdempotencyChecker,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("\${sqs.queue.payment-cancel:payment-cancel-queue}")
    fun handleCancelPaymentCommand(@Payload message: String) {
        logger.info("Received cancel payment command: {}", message)

        val command = try {
            objectMapper.readValue(message, CancelPaymentCommand::class.java)
        } catch (e: Exception) {
            logger.error("Failed to deserialize cancel payment command: {}", message, e)
            throw e
        }

        // 멱등성 체크: eventId 기준으로 이미 처리된 요청인지 확인
        val idempotencyKey = "cancel-payment-${command.eventId}"
        val alreadyProcessed = idempotencyChecker.checkAndRecord(idempotencyKey, "CancelPaymentCommand")

        if (alreadyProcessed) {
            logger.info("Cancel payment command already processed: orderId={}, eventId={}",
                command.orderId, command.eventId)
            return
        }

        try {
            val cancelCommand = PaymentCancelCommand(
                paymentKey = command.paymentKey,
                cancelReason = command.reason.toDisplayMessage()
            )

            val result = paymentCancelUseCase.cancel(cancelCommand)

            when (result.status) {
                PaymentStatus.CANCELED -> {
                    logger.info("Payment canceled successfully: orderId={}, paymentKey={}",
                        command.orderId, command.paymentKey)
                }
                PaymentStatus.FAILURE -> {
                    // 이미 취소된 결제인 경우 성공으로 처리 (멱등)
                    if (result.failure?.errorCode == "ALREADY_CANCELED") {
                        logger.info("Payment already canceled: orderId={}", command.orderId)
                    } else {
                        logger.error("Failed to cancel payment: orderId={}, error={}",
                            command.orderId, result.failure?.message)
                        throw RuntimeException("Payment cancellation failed: ${result.failure?.message}")
                    }
                }
                else -> {
                    logger.error("Unexpected cancellation result: orderId={}, status={}",
                        command.orderId, result.status)
                    throw RuntimeException("Unexpected cancellation result: ${result.status}")
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing cancel payment command: orderId={}", command.orderId, e)
            throw e // SQS will retry based on visibility timeout and eventually send to DLQ
        }
    }

    private fun com.example.payment.monolith.common.event.CancelReason.toDisplayMessage(): String {
        return when (this) {
            com.example.payment.monolith.common.event.CancelReason.FULFILLMENT_FAILED ->
                "지급 실패로 인한 결제 취소"
            com.example.payment.monolith.common.event.CancelReason.FULFILLMENT_TIMEOUT ->
                "지급 시도 타임아웃으로 인한 결제 취소"
            com.example.payment.monolith.common.event.CancelReason.FULFILLMENT_UNKNOWN ->
                "지급 상태 불확정으로 인한 결제 취소"
        }
    }
}