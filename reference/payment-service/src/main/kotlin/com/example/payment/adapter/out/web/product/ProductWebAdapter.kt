package com.example.payment.adapter.out.web.product

import com.example.common.WebAdapter
import com.example.payment.adapter.out.web.product.client.ProductClient
import com.example.payment.application.port.out.LoadProductPort
import com.example.payment.domain.Product
import reactor.core.publisher.Flux

@WebAdapter
class ProductWebAdapter (
  private val productClient: ProductClient
) : LoadProductPort {

  override fun getProducts(cartId: Long, productIds: List<Long>): Flux<Product> {
    return productClient.getProducts(cartId, productIds)
  }
}
