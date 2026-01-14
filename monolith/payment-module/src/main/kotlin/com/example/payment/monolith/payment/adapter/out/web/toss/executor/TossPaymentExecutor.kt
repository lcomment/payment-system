package com.example.payment.monolith.payment.adapter.out.web.toss.executor

import com.example.payment.monolith.payment.adapter.out.web.toss.exception.PSPConfirmationException
import com.example.payment.monolith.payment.adapter.out.web.toss.exception.TossPaymentError
import com.example.payment.monolith.payment.adapter.out.web.toss.response.TossFailureResponse
import com.example.payment.monolith.payment.adapter.out.web.toss.response.TossPaymentConfirmationResponse
import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.monolith.payment.application.port.out.PaymentExecutorPort
import com.example.payment.monolith.payment.domain.*
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class TossPaymentExecutor(
    private val tossPaymentWebClient: WebClient
) : PaymentExecutorPort {

    private val uri: String = "/v1/payments/confirm"

    override fun execute(command: PaymentConfirmCommand): PaymentExecutionResult {
        return try {
            val response = tossPaymentWebClient.post()
                .uri(uri)
                .header("Idempotency-Key", command.orderId)
                .bodyValue("""
                    {
                      "paymentKey": "${command.paymentKey}",
                      "orderId": "${command.orderId}",
                      "amount": ${command.amount}
                    }
                """.trimIndent())
                .retrieve()
                .onStatus({ statusCode: HttpStatusCode ->
                    statusCode.is4xxClientError || statusCode.is5xxServerError
                }) { response ->
                    response.bodyToMono(TossFailureResponse::class.java)
                        .map {
                            val error = TossPaymentError.get(it.code)
                            throw PSPConfirmationException(
                                errorCode = error.name,
                                errorMessage = error.description,
                                isSuccess = error.isSuccess(),
                                isFailure = error.isFailure(),
                                isUnknown = error.isUnknown(),
                                isRetryableError = error.isRetryableError()
                            )
                        }
                }
                .bodyToMono(TossPaymentConfirmationResponse::class.java)
                .block(Duration.ofSeconds(10))
                ?: throw RuntimeException("Toss payment confirmation failed: empty response")

            PaymentExecutionResult(
                paymentKey = command.paymentKey,
                orderId = command.orderId,
                extraDetails = PaymentExtraDetails(
                    type = PaymentType.get(response.type),
                    method = PaymentMethod.get(response.method),
                    approvedAt = LocalDateTime.parse(response.approvedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    pspRawData = response.toString(),
                    orderName = response.orderName,
                    pspConfirmationStatus = PSPConfirmationStatus.get(response.status),
                    totalAmount = response.totalAmount.toLong()
                ),
                isSuccess = true,
                isFailure = false,
                isUnknown = false,
                isRetryable = false
            )
        } catch (e: PSPConfirmationException) {
            PaymentExecutionResult(
                paymentKey = command.paymentKey,
                orderId = command.orderId,
                extraDetails = null,
                isSuccess = e.isSuccess,
                isFailure = e.isFailure,
                isUnknown = e.isUnknown,
                isRetryable = e.isRetryableError,
                failure = PaymentFailure(
                    errorCode = e.errorCode,
                    message = e.errorMessage
                )
            )
        }
    }
}
