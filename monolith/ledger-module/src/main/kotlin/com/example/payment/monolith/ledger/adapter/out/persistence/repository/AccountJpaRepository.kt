package com.example.payment.monolith.ledger.adapter.out.persistence.repository

import com.example.payment.monolith.ledger.adapter.out.persistence.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountJpaRepository : JpaRepository<AccountEntity, Long>
