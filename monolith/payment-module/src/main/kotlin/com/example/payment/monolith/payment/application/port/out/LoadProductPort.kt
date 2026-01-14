package com.example.payment.monolith.payment.application.port.out

import com.example.payment.monolith.payment.domain.Product

interface LoadProductPort {
    fun getProducts(cartId: Long, productIds: List<Long>): List<Product>
}
