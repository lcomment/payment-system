package com.example.wallet.application.port.out

import com.example.wallet.domain.Wallet

interface LoadWalletPort {

  fun getWallets(sellerIds: Set<Long>): Set<Wallet>
}
