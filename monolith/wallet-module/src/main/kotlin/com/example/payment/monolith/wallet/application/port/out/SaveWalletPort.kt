package com.example.payment.monolith.wallet.application.port.out

import com.example.payment.monolith.wallet.domain.Wallet

interface SaveWalletPort {
    fun save(wallets: List<Wallet>)
}
