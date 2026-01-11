package com.example.wallet.adapter.out.persistence.repository

import com.example.wallet.domain.PaymentEventMessage
import com.example.wallet.domain.WalletTransaction

interface WalletTransactionRepository {

  fun isExist(paymentEventMessage: PaymentEventMessage): Boolean

  fun save(walletTransactions: List<WalletTransaction>)
}
