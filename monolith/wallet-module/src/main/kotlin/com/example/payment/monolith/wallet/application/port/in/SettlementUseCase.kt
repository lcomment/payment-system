package com.example.payment.monolith.wallet.application.port.`in`

import com.example.payment.monolith.payment.domain.event.PaymentConfirmedEvent

interface SettlementUseCase {
    fun processSettlement(event: PaymentConfirmedEvent)

    /**
     * 오케스트레이터에서 호출 시 사용 - 이벤트 발행 없이 정산 처리만 수행
     */
    fun processSettlementWithoutEvent(event: PaymentConfirmedEvent)
}
