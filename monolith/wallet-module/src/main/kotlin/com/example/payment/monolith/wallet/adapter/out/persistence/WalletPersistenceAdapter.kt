package com.example.payment.monolith.wallet.adapter.out.persistence

import com.example.payment.monolith.common.annotations.PersistenceAdapter
import com.example.payment.monolith.wallet.adapter.out.persistence.entity.WalletMapper
import com.example.payment.monolith.wallet.adapter.out.persistence.repository.WalletJpaRepository
import com.example.payment.monolith.wallet.adapter.out.persistence.repository.WalletTransactionJpaRepository
import com.example.payment.monolith.wallet.application.port.out.LoadWalletPort
import com.example.payment.monolith.wallet.application.port.out.SaveWalletPort
import com.example.payment.monolith.wallet.domain.Wallet
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@PersistenceAdapter
class WalletPersistenceAdapter(
    private val walletJpaRepository: WalletJpaRepository,
    private val walletTransactionJpaRepository: WalletTransactionJpaRepository,
    private val walletMapper: WalletMapper
) : LoadWalletPort, SaveWalletPort {

    @Transactional(readOnly = true)
    override fun getWallets(userIds: Set<Long>): Set<Wallet> {
        return walletJpaRepository.findByUserIdIn(userIds)
            .map { walletMapper.mapToDomainEntity(it) }
            .toSet()
    }

    @Transactional
    override fun save(wallets: List<Wallet>) {
        try {
            performSaveOperation(wallets)
        } catch (e: OptimisticLockingFailureException) {
            retrySaveOperation(wallets)
        }
    }

    private fun performSaveOperation(wallets: List<Wallet>) {
        // Save updated wallet balances
        val walletEntities = wallets.map { walletMapper.mapToJpaEntity(it) }
        walletJpaRepository.saveAll(walletEntities)

        // Save wallet transactions
        val transactionEntities = wallets.flatMap { wallet ->
            wallet.walletTransactions.map { transaction ->
                walletMapper.mapToJpaTransactionEntity(
                    transaction,
                    idempotencyKey = "${transaction.orderId}-${transaction.referenceId}"
                )
            }
        }
        walletTransactionJpaRepository.saveAll(transactionEntities)
    }

    private fun retrySaveOperation(wallets: List<Wallet>, maxRetries: Int = 3) {
        var retryCount = 0

        while (true) {
            try {
                performSaveOperationWithRecent(wallets)
                break
            } catch (e: OptimisticLockingFailureException) {
                if (++retryCount > maxRetries) {
                    throw RuntimeException("Exhausted retry count for optimistic locking", e)
                }
                Thread.sleep((Math.random() * 100).toLong())
            }
        }
    }

    private fun performSaveOperationWithRecent(wallets: List<Wallet>) {
        val recentWallets = walletJpaRepository.findByIdIn(wallets.map { it.id }.toSet())
        val recentWalletsById = recentWallets.associateBy { it.id }

        val updatedWallets = wallets.map { wallet ->
            val recentWallet = recentWalletsById[wallet.id]!!
            val transactionSum = BigDecimal(wallet.walletTransactions.sumOf { it.amount })
            recentWallet.addBalance(transactionSum)
        }

        walletJpaRepository.saveAll(updatedWallets)

        val transactionEntities = wallets.flatMap { wallet ->
            wallet.walletTransactions.map { transaction ->
                walletMapper.mapToJpaTransactionEntity(
                    transaction,
                    idempotencyKey = "${transaction.orderId}-${transaction.referenceId}"
                )
            }
        }
        walletTransactionJpaRepository.saveAll(transactionEntities)
    }
}
