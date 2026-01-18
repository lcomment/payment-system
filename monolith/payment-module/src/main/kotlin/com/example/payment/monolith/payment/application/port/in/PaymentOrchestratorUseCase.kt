package com.example.payment.monolith.payment.application.port.`in`

import com.example.payment.monolith.payment.domain.PaymentConfirmationResult

/**
 * PaymentOrchestratorUseCase - 결제 승인 및 Wallet/Ledger 처리를 단일 트랜잭션으로 오케스트레이션
 *
 * Fulfillment에서 동기 호출하여 사용
 */
interface PaymentOrchestratorUseCase {
    fun confirmAndProcess(command: PaymentConfirmCommand): PaymentConfirmationResult
}