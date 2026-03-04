package main.kotlin.core.domain.repository

import main.kotlin.core.domain.models.Product

interface ProductRepository {

    fun getById(id: Long): Product?
    fun save(product: Product)
    fun getAll(): List<Product>
}
