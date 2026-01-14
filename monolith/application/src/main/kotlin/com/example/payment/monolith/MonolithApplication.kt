package com.example.payment.monolith

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    scanBasePackages = [
        "com.example.payment.monolith.common",
        "com.example.payment.monolith.payment",
        "com.example.payment.monolith.wallet",
        "com.example.payment.monolith.ledger"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "com.example.payment.monolith.common",
        "com.example.payment.monolith.payment.adapter.out.persistence.repository",
        "com.example.payment.monolith.wallet.adapter.out.persistence.repository",
        "com.example.payment.monolith.ledger.adapter.out.persistence.repository"
    ]
)
@EntityScan(
    basePackages = [
        "com.example.payment.monolith.common.outbox",
        "com.example.payment.monolith.common.idempotency",
        "com.example.payment.monolith.payment.adapter.out.persistence.entity",
        "com.example.payment.monolith.wallet.adapter.out.persistence.entity",
        "com.example.payment.monolith.ledger.adapter.out.persistence.entity"
    ]
)
@EnableAsync
@EnableScheduling
class MonolithApplication

fun main(args: Array<String>) {
    runApplication<MonolithApplication>(*args)
}
