package com.example.payment.monolith.payment.application.service

import com.example.payment.monolith.common.annotations.UseCase
import com.example.payment.monolith.common.idempotency.IdempotencyChecker
import com.example.payment.monolith.ledger.application.port.`in`.DoubleLedgerEntryRecordUseCase
import com.example.payment.monolith.payment.application.port.`in`.PaymentConfirmCommand
import com.example.payment.monolith.payment.application.port.`in`.PaymentOrchestratorUseCase
import com.example.payment.monolith.payment.application.port.out.CompletePaymentPort
import com.example.payment.monolith.payment.application.port.out.LoadPaymentPort
import com.example.payment.monolith.payment.application.port.out.PaymentExecutorPort
import com.example.payment.monolith.payment.application.port.out.PaymentStatusUpdateCommand
import com.example.payment.monolith.payment.application.port.out.PaymentStatusUpdatePort
import com.example.payment.monolith.payment.application.port.out.PaymentValidationPort
import com.example.payment.monolith.payment.domain.PaymentConfirmationResult
import com.example.payment.monolith.payment.domain.PaymentStatus
import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent
import com.example.payment.monolith.wallet.application.port.`in`.SettlementUseCase
import org.springframework.transaction.annotation.Transactional

/**
 * PaymentOrchestratorService - 결제 승인 후 Wallet/Ledger를 단일 트랜잭션으로 처리
 *
 * 문서 정책:
 * - PG 승인 성공 후 Payment/Wallet/Ledger를 하나의 트랜잭션으로 묶어서 처리
 * - 큐 없이 동기 호출로 정합성 보장
 */
@UseCase
class PaymentOrchestratorService(
    private val paymentStatusUpdatePort: PaymentStatusUpdatePort,
    private val paymentValidationPort: PaymentValidationPort,
    private val paymentExecutorPort: PaymentExecutorPort,
    private val loadPaymentPort: LoadPaymentPort,
    private val completePaymentPort: CompletePaymentPort,
    private val settlementUseCase: SettlementUseCase,
    private val doubleLedgerEntryRecordUseCase: DoubleLedgerEntryRecordUseCase,
    private val idempotencyChecker: IdempotencyChecker
) : PaymentOrchestratorUseCase {

    @Transactional
    override fun confirmAndProcess(command: PaymentConfirmCommand): PaymentConfirmationResult {
        // 멱등성 체크: orderId 기준으로 이미 처리된 요청인지 확인
        val idempotencyKey = "payment-confirm-${command.orderId}"
        val alreadyProcessed = idempotencyChecker.checkAndRecord(idempotencyKey, "PaymentConfirm")

        if (alreadyProcessed) {
            val existingPayment = loadPaymentPort.getPayment(command.orderId)
            return PaymentConfirmationResult(
                status = if (existingPayment.isSuccess()) PaymentStatus.SUCCESS else PaymentStatus.FAILURE,
                failure = null
            )
        }

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

        // 5. If success, process Wallet and Ledger in the same transaction
        if (executionResult.isSuccess) {
            val paymentEvent = loadPaymentPort.getPayment(command.orderId)
            val event = PaymentConfirmedEvent.from(paymentEvent)

            // Wallet 정산 처리 (동기, 같은 트랜잭션, 이벤트 발행 없음)
            settlementUseCase.processSettlementWithoutEvent(event)

            // Ledger 복식부기 기록 (동기, 같은 트랜잭션, 이벤트 발행 없음)
            doubleLedgerEntryRecordUseCase.recordDoubleLedgerEntryWithoutEvent(event)

            // Payment 상태를 완료로 업데이트
            paymentEvent.confirmWalletUpdate()
            paymentEvent.confirmLedgerUpdate()
            paymentEvent.completeIfDone()
            completePaymentPort.complete(paymentEvent)
        }

        return PaymentConfirmationResult(
            status = executionResult.paymentStatus(),
            failure = executionResult.failure
        )
    }
}
