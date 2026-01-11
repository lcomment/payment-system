package com.example.wallet.adapter.out.persistence.repository

import com.example.wallet.domain.Wallet

interface WalletRepository {

  fun getWallets(sellerIds: Set<Long>): Set<Wallet>

  fun save(wallets: List<Wallet>)
}
