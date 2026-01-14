package com.example.payment.monolith.wallet.application.port.out

import com.example.payment.monolith.wallet.domain.Wallet

interface LoadWalletPort {
    fun getWallets(userIds: Set<Long>): Set<Wallet>
}
