package com.example.payment.monolith.ledger.domain

data class DoubleAccountsForLedger(
    val to: Account,
    val from: Account
)
