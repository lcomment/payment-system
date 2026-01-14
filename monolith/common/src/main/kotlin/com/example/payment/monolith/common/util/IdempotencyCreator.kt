package com.example.payment.monolith.common.util

import java.security.MessageDigest

object IdempotencyCreator {

    fun create(data: Any): String {
        val bytes = data.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
