package com.example.ledger.adapter.out.persistence.repository

import com.example.ledger.domain.DoubleAccountsForLedger
import com.example.ledger.domain.FinanceType

interface AccountRepository {

  fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger
}
