package com.example.payment.monolith.wallet.adapter.out.persistence.repository

import com.example.payment.monolith.wallet.adapter.out.persistence.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletJpaRepository : JpaRepository<WalletEntity, Long> {

    fun findByUserIdIn(userIds: Set<Long>): List<WalletEntity>

    fun findByIdIn(ids: Set<Long>): List<WalletEntity>
}
