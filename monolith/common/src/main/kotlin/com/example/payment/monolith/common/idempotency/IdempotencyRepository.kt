package com.example.payment.monolith.common.idempotency

import org.springframework.data.jpa.repository.JpaRepository

interface IdempotencyRepository : JpaRepository<IdempotencyKey, String> {
    fun existsByIdempotencyKey(key: String): Boolean
}
