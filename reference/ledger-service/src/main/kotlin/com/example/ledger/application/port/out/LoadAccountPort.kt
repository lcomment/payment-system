package com.example.ledger.application.port.out

import com.example.ledger.domain.DoubleAccountsForLedger
import com.example.ledger.domain.FinanceType

interface LoadAccountPort {

  fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger
}
