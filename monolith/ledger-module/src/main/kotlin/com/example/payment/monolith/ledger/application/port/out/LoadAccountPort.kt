package com.example.payment.monolith.ledger.application.port.out

import com.example.payment.monolith.ledger.domain.DoubleAccountsForLedger
import com.example.payment.monolith.ledger.domain.FinanceType

interface LoadAccountPort {
    fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger
}
