package com.example.wallet.application.port.out

import com.example.wallet.domain.Wallet

interface SaveWalletPort {

  fun save(wallets: List<Wallet>)
}
