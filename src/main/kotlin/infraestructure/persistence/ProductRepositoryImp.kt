package main.kotlin.infraestructure.persistence

import main.kotlin.core.domain.models.Product
import main.kotlin.core.domain.repository.ProductRepository

class ProductRepositoryImp : ProductRepository {

    private val products = mutableMapOf<Long, Product>()

    override fun getById(id: Long): Product? {
        return products[id]
    }

    override fun save(product: Product) {
        products[product.id] = product
    }

    override fun getAll(): List<Product> {
        return products.values.toList()
    }
}