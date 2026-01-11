package com.example.ledger.adapter.out.persistence

import com.example.common.PersistenceAdapter
import com.example.ledger.adapter.out.persistence.repository.AccountRepository
import com.example.ledger.application.port.out.LoadAccountPort
import com.example.ledger.domain.DoubleAccountsForLedger
import com.example.ledger.domain.FinanceType

@PersistenceAdapter
class AccountPersistenceAdapter (
  private val accountRepository: AccountRepository
) : LoadAccountPort {

  override fun getDoubleAccountsForLedger(financeType: FinanceType): DoubleAccountsForLedger {
    return accountRepository.getDoubleAccountsForLedger(financeType)
  }
}
