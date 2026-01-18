package com.example.payment.monolith.payment.adapter.`in`.web

import com.example.payment.monolith.common.annotations.WebAdapter
import com.example.payment.monolith.payment.adapter.`in`.web.request.TossPaymentCancelRequest
import com.example.payment.monolith.payment.adapter.`in`.web.request.TossPaymentConfirmRequest
import com.example.payment.monolith.payment.adapter.`in`.web.response.ApiResponse
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentCancelUseCase
import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentOrchestratorUseCase
import com.example.payment.monolith.payment.domain.PaymentCancellationResult
import com.example.payment.monolith.payment.domain.PaymentConfirmationResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WebAdapter
@RestController
@RequestMapping("/v1/toss")
class TossPaymentController(
    private val paymentOrchestratorUseCase: PaymentOrchestratorUseCase,
    private val paymentCancelUseCase: PaymentCancelUseCase
) {

    @PostMapping("/confirm")
    fun confirm(@RequestBody request: TossPaymentConfirmRequest): ResponseEntity<ApiResponse<PaymentConfirmationResult>> {
        val command = PaymentConfirmCommand(
            paymentKey = request.paymentKey,
            orderId = request.orderId,
            amount = request.amount.toLong()
        )

        val result = paymentOrchestratorUseCase.confirmAndProcess(command)

        return ResponseEntity.ok()
            .body(ApiResponse.with(HttpStatus.OK, "", result))
    }

    @PostMapping("/cancel")
    fun cancel(@RequestBody request: TossPaymentCancelRequest): ResponseEntity<ApiResponse<PaymentCancellationResult>> {
        val command = PaymentCancelCommand(
            paymentKey = request.paymentKey,
            cancelReason = request.cancelReason
        )

        val result = paymentCancelUseCase.cancel(command)

        return ResponseEntity.ok()
            .body(ApiResponse.with(HttpStatus.OK, "", result))
    }
}
