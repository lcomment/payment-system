package com.example.payment.application.port.out

import com.example.payment.domain.Product
import reactor.core.publisher.Flux

interface LoadProductPort {

  fun getProducts(cartId: Long, productIds: List<Long>): Flux<Product>
}
