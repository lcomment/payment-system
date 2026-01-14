package com.example.payment.monolith.common.idempotency

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class IdempotencyChecker(
    private val idempotencyRepository: IdempotencyRepository
) {
    @Transactional
    fun checkAndRecord(key: String, eventType: String): Boolean {
        if (idempotencyRepository.existsByIdempotencyKey(key)) {
            return true // Already processed
        }
        idempotencyRepository.save(IdempotencyKey(key, eventType))
        return false // Not processed yet
    }
}
