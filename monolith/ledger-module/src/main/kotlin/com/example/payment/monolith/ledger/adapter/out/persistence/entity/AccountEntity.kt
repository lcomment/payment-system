package com.example.payment.monolith.ledger.adapter.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    val id: Long? = null,

    val name: String
)
