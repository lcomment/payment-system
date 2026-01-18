package com.example.payment.monolith.ledger.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent

interface DoubleLedgerEntryRecordUseCase {
    fun recordDoubleLedgerEntry(event: PaymentConfirmedEvent)

    /**
     * 오케스트레이터에서 호출 시 사용 - 이벤트 발행 없이 원장 기록만 수행
     */
    fun recordDoubleLedgerEntryWithoutEvent(event: PaymentConfirmedEvent)
}
