package com.example.payment.monolith.common.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@ConditionalOnProperty(name = ["outbox.sqs.enabled"], havingValue = "true", matchIfMissing = false)
class OutboxSqsWorker(
    private val outboxRepository: OutboxRepository,
    private val sqsTemplate: SqsTemplate,
    private val objectMapper: ObjectMapper
) {
    @Scheduled(fixedDelay = 5000) // Poll every 5 seconds
    @Transactional
    fun processOutbox() {
        val pendingEvents = outboxRepository.findByStatusAndRetryCountLessThan(
            status = OutboxStatus.PENDING,
            retryCount = 3
        )

        if (pendingEvents.isEmpty()) {
            return
        }

        pendingEvents.forEach { outboxEvent ->
            try {
                val queueName = determineQueueName(outboxEvent.eventType)
                sqsTemplate.send(queueName, outboxEvent.payload)

                outboxRepository.save(
                    outboxEvent.copy(
                        status = OutboxStatus.SENT,
                        sentAt = LocalDateTime.now()
                    )
                )
            } catch (e: Exception) {
                outboxRepository.save(
                    outboxEvent.copy(
                        retryCount = outboxEvent.retryCount + 1,
                        status = if (outboxEvent.retryCount >= 2) OutboxStatus.FAILED else OutboxStatus.PENDING
                    )
                )
            }
        }
    }

    private fun determineQueueName(eventType: String): String {
        return when (eventType) {
            "PaymentConfirmedEvent" -> "payment-queue"
            "PaymentCanceledEvent" -> "payment-queue"
            "WalletSettledEvent" -> "wallet-queue"
            "WalletRefundedEvent" -> "wallet-queue"
            "LedgerRecordedEvent" -> "ledger-queue"
            "LedgerReversedEvent" -> "ledger-queue"
            "CancelPaymentCommand" -> "payment-cancel-queue"
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
    }
}
